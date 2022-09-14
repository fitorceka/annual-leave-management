package com.lhind.annualleavemanagement.leave.service;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.leave.repository.LeaveRepository;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
import com.lhind.annualleavemanagement.util.DateUtils;
import com.lhind.annualleavemanagement.util.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lhind.annualleavemanagement.util.Constants.MANAGER_ACCEPTED_LEAVE;
import static com.lhind.annualleavemanagement.util.Constants.MANAGER_REJECTED_LEAVE;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public LeaveEntity findLeaveById(Long id) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception(Constants.LEAVE_CANNOT_BE_FOUND_BY_ID.replace("leaveId", String.valueOf(id))));
    }

    @Transactional(readOnly = true)
    public List<LeaveEntity> findAllEmployeeLeavesUnderManager() throws Exception {
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
        return Optional.of(CurrentAuthenticatedUser.getCurrentUser()).map(user -> userService.findUserById(user.getId())).orElse(null).getLeaves();
    }

    public void saveLeaveToCurrentlyAuthenticatedUser(LeaveEntity leaveEntity) throws Exception {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity authenticatedUserEntity = userService.findUserById(user.getId());

        if (!authenticatedUserEntity.getRole().equals(Constants.ROLE_EMPLOYEE)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST);
        }

        long noOfDays = Duration.between(leaveEntity.getFromDate().atStartOfDay(), leaveEntity.getToDate().atStartOfDay()).toDays();
        long daysFromHire = authenticatedUserEntity.getDaysFromHire();

        if (leaveEntity.getFromDate().isBefore(DateUtils.fetchDateAndTimeOfCurrentMachine())) {
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
        authenticatedUserEntity.setAnnualLeaveDays((int) (authenticatedUserEntity.getAnnualLeaveDays() - leaveEntity.getNoOfDays()));
        repository.save(leaveEntity);

        emailService.sendMailToManager(leaveEntity.getLeaveReason(), "Employee: " + user.getFullName() + " created new leave request");
    }

    public void updateLeave(Long leaveId, String leaveReason, LocalDate fromDate, LocalDate toDate) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        UserEntity userEntity = leaveEntity.getUser();

        int userLeaveDays = (int) (userEntity.getAnnualLeaveDays() + leaveEntity.getNoOfDays());

        leaveEntity.setLeaveReason(leaveReason);
        leaveEntity.setFromDate(fromDate);
        leaveEntity.setToDate(toDate);
        leaveEntity.setNoOfDays(Duration.between(leaveEntity.getFromDate().atStartOfDay(), leaveEntity.getToDate().atStartOfDay()).toDays());
        userEntity.setAnnualLeaveDays((int) (userLeaveDays - leaveEntity.getNoOfDays()));
        repository.save(leaveEntity);

        emailService.sendMailToManager(leaveEntity.getLeaveReason(), "Employee: " + userEntity.getFirstName() + " " + userEntity.getLastName() + " updated a leave request");
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

        emailService.sendMailToEmployee(userEntityThatCreatedTheLeave, leaveEntity.getLeaveReason(), MANAGER_ACCEPTED_LEAVE.replace("manager", user.getFullName()));
    }

    public void rejectLeaveRequest(Long leaveId) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(leaveId);

        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity authenticatedUserEntity = userService.findUserById(user.getId());

        if (!authenticatedUserEntity.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        UserEntity userEntityThatCreatedTheLeave = leaveEntity.getUser();
        userEntityThatCreatedTheLeave.setAnnualLeaveDays((int) (userEntityThatCreatedTheLeave.getAnnualLeaveDays() + leaveEntity.getNoOfDays()));
        leaveEntity.setStatus(Constants.STATUS_REJECTED);

        emailService.sendMailToEmployee(userEntityThatCreatedTheLeave, leaveEntity.getLeaveReason(), MANAGER_REJECTED_LEAVE.replace("manager", user.getFullName()));
    }

    public void deleteLeave(Long id) throws Exception {
        LeaveEntity leaveEntity = findLeaveById(id);

        UserEntity userEntity = leaveEntity.getUser();

        userEntity.setAnnualLeaveDays((int) (userEntity.getAnnualLeaveDays() + leaveEntity.getNoOfDays()));

        repository.delete(leaveEntity);
    }
}
