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
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
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
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity manager = userService.findUserById(user.getId());

        if (!manager.getRole().equals(Constants.ROLE_MANAGER)) {
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
            .map(user -> userService.findUserById(user.getId()))
            .orElse(null)
            .getLeaves();
    }

    public void saveLeaveToCurrentlyAuthenticatedUser(LeaveEntity leaveEntity) {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity authenticatedUserEntity = userService.findUserById(user.getId());

        if (!authenticatedUserEntity.getRole().equals(Constants.ROLE_EMPLOYEE)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST);
        }

        int noOfDays = (int) Duration
            .between(leaveEntity.getFromDate().atStartOfDay(), leaveEntity.getToDate().atStartOfDay())
            .toDays();
        int daysFromHire = authenticatedUserEntity.getDaysFromHire();

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

        leaveEntity.setUser(authenticatedUserEntity);
        leaveEntity.setStatus(Constants.STATUS_PENDING);
        leaveEntity.setNoOfDays(noOfDays);
        authenticatedUserEntity
            .setAnnualLeaveDays((authenticatedUserEntity.getAnnualLeaveDays() - leaveEntity.getNoOfDays()));
        repository.save(leaveEntity);

        emailService
            .sendMailToManager(leaveEntity.getLeaveReason(), String.format(USER_CREATED_LEAVE, user.getFullName()));
    }

    public void updateLeave(Long leaveId, String leaveReason, LocalDate fromDate, LocalDate toDate) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        UserEntity userEntity = leaveEntity.getUser();

        int userLeaveDays = userEntity.getAnnualLeaveDays() + leaveEntity.getNoOfDays();

        leaveEntity.setLeaveReason(leaveReason);
        leaveEntity.setFromDate(fromDate);
        leaveEntity.setToDate(toDate);
        leaveEntity.setNoOfDays((int) Duration.between(leaveEntity.getFromDate(), leaveEntity.getToDate()).toDays());
        userEntity.setAnnualLeaveDays(userLeaveDays - leaveEntity.getNoOfDays());
        repository.save(leaveEntity);

        emailService
            .sendMailToManager(leaveEntity.getLeaveReason(),
                String.format(USER_UPDATED_LEAVE, userEntity.getFirstName(), userEntity.getLastName()));
    }

    public void acceptLeaveRequest(Long leaveId) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity authenticatedUserEntity = userService.findUserById(user.getId());

        if (!authenticatedUserEntity.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        UserEntity userEntityThatCreatedTheLeave = leaveEntity.getUser();
        leaveEntity.setStatus(Constants.STATUS_ACCEPTED);

        emailService
            .sendMailToEmployee(userEntityThatCreatedTheLeave, leaveEntity.getLeaveReason(),
                String.format(MANAGER_ACCEPTED_LEAVE, user.getFullName()));
    }

    public void rejectLeaveRequest(Long leaveId) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity authenticatedUserEntity = userService.findUserById(user.getId());

        if (!authenticatedUserEntity.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        UserEntity userEntityThatCreatedTheLeave = leaveEntity.getUser();
        userEntityThatCreatedTheLeave
            .setAnnualLeaveDays(userEntityThatCreatedTheLeave.getAnnualLeaveDays() + leaveEntity.getNoOfDays());
        leaveEntity.setStatus(Constants.STATUS_REJECTED);

        emailService
            .sendMailToEmployee(userEntityThatCreatedTheLeave, leaveEntity.getLeaveReason(),
                String.format(MANAGER_REJECTED_LEAVE, user.getFullName()));
    }

    public void deleteLeave(Long id) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(id);

        UserEntity userEntity = leaveEntity.getUser();

        userEntity.setAnnualLeaveDays(userEntity.getAnnualLeaveDays() + leaveEntity.getNoOfDays());

        repository.delete(leaveEntity);
    }
}
