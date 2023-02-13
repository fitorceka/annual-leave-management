package com.lhind.annualleavemanagement.leave.service;

import static com.lhind.annualleavemanagement.util.Constants.MANAGER_ACCEPTED_LEAVE;
import static com.lhind.annualleavemanagement.util.Constants.MANAGER_REJECTED_LEAVE;
import static com.lhind.annualleavemanagement.util.Constants.USER_CREATED_LEAVE;
import static com.lhind.annualleavemanagement.util.Constants.USER_UPDATED_LEAVE;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.leave.repository.LeaveRepository;
import com.lhind.annualleavemanagement.security.CurrentAuthenticatedUser;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.enums.LeaveReason;
import com.lhind.annualleavemanagement.util.enums.Role;
import com.lhind.annualleavemanagement.util.enums.Status;
import com.lhind.annualleavemanagement.util.service.EmailService;

@Service
@Transactional
public class LeaveService {

    private final LeaveRepository repository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public LeaveService(LeaveRepository repository, UserService userService, UserRepository userRepository,
            EmailService emailService) {
        this.repository = repository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public LeaveEntity findLeaveById(Long id) throws Exception {
        return repository
            .findById(id)
            .orElseThrow(() -> new Exception(Constants.LEAVE_CANNOT_BE_FOUND_BY_ID.replace("leaveId", String.valueOf(id))));
    }

    @Transactional(readOnly = true)
    public List<LeaveEntity> findAllEmployeeLeavesUnderManager() {
        UserEntity manager = userService.findUserById(CurrentAuthenticatedUser.getCurrentUser().getUserId());

        if (!(manager.getRole() == Role.MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        List<UserEntity> usersUnderManager = userRepository.findAllUsersUnderManager(manager.getUserId());
        List<LeaveEntity> leaves = new ArrayList<>();

        usersUnderManager.forEach(u -> leaves.addAll(u.getLeaves()));

        return leaves;
    }

    @Transactional(readOnly = true)
    public List<LeaveEntity> findAllLeavesForAuthenticatedUser() {
        return Optional
            .of(CurrentAuthenticatedUser.getCurrentUser())
            .map(UserEntity::getLeaves)
            .orElse(null);
    }

    public void saveLeaveToCurrentlyAuthenticatedUser(LeaveEntity leaveEntity) {
        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!(user.getRole() == Role.EMPLOYEE)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST);
        }

        int noOfDays = (int) Duration
            .between(leaveEntity.getFromDate().atStartOfDay(), leaveEntity.getToDate().atStartOfDay())
            .toDays();
        int daysFromHire = user.getDaysFromHire();

        if (leaveEntity.getFromDate().isBefore(LocalDate.now())) {
            throw new RuntimeException(Constants.FROM_DATE_CANNOT_BE_SET_BEFORE_CURRENT_DATE);
        }

        if (leaveEntity.getToDate().isBefore(leaveEntity.getFromDate())) {
            throw new RuntimeException(Constants.TO_DATE_CANNOT_BE_SET_BEFORE_FROM_DATE);
        }

        if (noOfDays > 20) {
            throw new RuntimeException(Constants.LEAVE_MORE_THAN_20_DAYS);
        }

        if (daysFromHire < 90) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST_PROBATION_PERIOD);
        }

        leaveEntity.setUser(user);
        leaveEntity.setStatus(Status.PENDING);
        leaveEntity.setNoOfDays(noOfDays);

        user
            .setAnnualLeaveDays((user.getAnnualLeaveDays() - leaveEntity.getNoOfDays()));
        repository.save(leaveEntity);

        emailService
            .sendMailToManager(leaveEntity.getLeaveReason().getValue(), String.format(USER_CREATED_LEAVE, user.getFullName()));
    }

    public void updateLeave(Long leaveId, String leaveReason, LocalDate fromDate, LocalDate toDate) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        UserEntity userEntity = leaveEntity.getUser();

        int userLeaveDays = userEntity.getAnnualLeaveDays() + leaveEntity.getNoOfDays();

        leaveEntity.setLeaveReason(LeaveReason.valueOf(leaveReason));
        leaveEntity.setFromDate(fromDate);
        leaveEntity.setToDate(toDate);
        leaveEntity.setNoOfDays((int) Duration.between(leaveEntity.getFromDate(), leaveEntity.getToDate()).toDays());
        userEntity.setAnnualLeaveDays(userLeaveDays - leaveEntity.getNoOfDays());
        repository.save(leaveEntity);

        emailService
            .sendMailToManager(leaveEntity.getLeaveReason().name(),
                String.format(USER_UPDATED_LEAVE, userEntity.getFirstName(), userEntity.getLastName()));
    }

    public void acceptLeaveRequest(Long leaveId) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!(user.getRole() == Role.MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        UserEntity userThatCreatedTheLeave = leaveEntity.getUser();

        leaveEntity.setStatus(Status.ACCEPTED);

        emailService
            .sendMailToEmployee(userThatCreatedTheLeave, leaveEntity.getLeaveReason().name(),
                String.format(MANAGER_ACCEPTED_LEAVE, user.getFullName()));
    }

    public void rejectLeaveRequest(Long leaveId) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!(user.getRole() == Role.MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        UserEntity userThatCreatedTheLeave = leaveEntity.getUser();

        userThatCreatedTheLeave
            .setAnnualLeaveDays(userThatCreatedTheLeave.getAnnualLeaveDays() + leaveEntity.getNoOfDays());

        leaveEntity.setStatus(Status.REJECTED);

        emailService
            .sendMailToEmployee(userThatCreatedTheLeave, leaveEntity.getLeaveReason().name(),
                String.format(MANAGER_REJECTED_LEAVE, user.getFullName()));
    }

    public void deleteLeave(Long id) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(id);

        UserEntity userEntity = leaveEntity.getUser();

        userEntity.setAnnualLeaveDays(userEntity.getAnnualLeaveDays() + leaveEntity.getNoOfDays());

        repository.delete(leaveEntity);
    }
}
