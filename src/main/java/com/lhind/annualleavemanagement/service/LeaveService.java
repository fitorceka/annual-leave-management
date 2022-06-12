package com.lhind.annualleavemanagement.service;

import com.lhind.annualleavemanagement.entity.Leave;
import com.lhind.annualleavemanagement.entity.User;
import com.lhind.annualleavemanagement.repository.LeaveRepository;
import com.lhind.annualleavemanagement.repository.UserRepository;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
import com.lhind.annualleavemanagement.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Leave findLeaveById(Long id) throws Exception {
        Optional<Leave> leave = leaveRepository.findById(id);
        if (leave.isPresent()) {
            return leave.get();
        }
        else {
            throw new Exception(Constants.LEAVE_CANNOT_BE_FOUND_BY_ID.replace("leaveId", String.valueOf(id)));
        }
    }

    @Transactional(readOnly = true)
    public List<Leave> findAllEmployeeLeavesUnderManager() throws Exception {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User manager = userService.findUserById(user.getId());

        if (!manager.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        List<User> usersUnderManager = userRepository.findAllUsersUnderManager(manager.getUserId());
        List<Leave> leaves = new ArrayList<>();

        usersUnderManager.forEach(u -> leaves.addAll(u.getLeaveList()));

        return leaves;
    }

    @Transactional(readOnly = true)
    public List<Leave> findAllLeavesForAuthenticatedUser() throws Exception {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User authenticatedUser = userService.findUserById(user.getId());

        return authenticatedUser.getLeaveList();
    }

    public void saveLeaveToCurrentlyAuthenticatedUser(Leave leave) throws Exception {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User authenticatedUser = userService.findUserById(user.getId());

        if (!authenticatedUser.getRole().equals(Constants.ROLE_EMPLOYEE)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST);
        }

        long noOfDays = Duration.between(leave.getFromDate().atStartOfDay(), leave.getToDate().atStartOfDay()).toDays();
        long daysFromHire = authenticatedUser.getDaysFromHire();

        if (leave.getFromDate().isBefore(DateUtils.fetchDateAndTimeOfCurrentMachine())) {
            throw new RuntimeException(Constants.FROM_DATE_CANNOT_BE_SET_BEFORE_CURRENT_DATE);
        }

        if (leave.getToDate().isBefore(leave.getFromDate())) {
            throw new RuntimeException(Constants.TO_DATE_CANNOT_BE_SET_BEFORE_FROM_DATE);
        }

        if (noOfDays > 20) {
            throw new RuntimeException(Constants.LEAVE_MORE_THAN_20_DAYS);
        }

        if (daysFromHire < 90) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_CANNOT_CREATE_AN_LEAVE_REQUEST_PROBATION_PERIOD);
        }

        leave.setUser(authenticatedUser);
        leave.setStatus(Constants.STATUS_PENDING);
        leave.setNoOfDays(noOfDays);
        authenticatedUser.setAnnualLeaveDays((int) (authenticatedUser.getAnnualLeaveDays() - leave.getNoOfDays()));
        leaveRepository.save(leave);

        emailService.sendMailToManager(leave.getLeaveReason(), "Employee: " + user.getFullName() + " created new leave request");
    }

    public void updateLeave(Long leaveId, String leaveReason, LocalDate fromDate, LocalDate toDate) throws Exception {
        Leave leave = findLeaveById(leaveId);

        User user = leave.getUser();

        int userLeaveDays = (int) (user.getAnnualLeaveDays() + leave.getNoOfDays());

        leave.setLeaveReason(leaveReason);
        leave.setFromDate(fromDate);
        leave.setToDate(toDate);
        leave.setNoOfDays(Duration.between(leave.getFromDate().atStartOfDay(), leave.getToDate().atStartOfDay()).toDays());
        user.setAnnualLeaveDays((int) (userLeaveDays - leave.getNoOfDays()));
        leaveRepository.save(leave);

        emailService.sendMailToManager(leave.getLeaveReason(), "Employee: " + user.getFirstName() + " " + user.getLastName() + " updated a leave request");
    }

    public void acceptLeaveRequest(Long leaveId) throws Exception {
        Leave leave = findLeaveById(leaveId);

        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User authenticatedUser = userService.findUserById(user.getId());

        if (!authenticatedUser.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        User userThatCreatedTheLeave = leave.getUser();
        leave.setStatus(Constants.STATUS_ACCEPTED);

        emailService.sendMailToEmployee(userThatCreatedTheLeave, leave.getLeaveReason(), "Your manager: " + user.getFullName() + " accepted your leave request");
    }

    public void rejectLeaveRequest(Long leaveId) throws Exception {
        Leave leave = findLeaveById(leaveId);

        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User authenticatedUser = userService.findUserById(user.getId());

        if (!authenticatedUser.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        User userThatCreatedTheLeave = leave.getUser();
        userThatCreatedTheLeave.setAnnualLeaveDays((int) (userThatCreatedTheLeave.getAnnualLeaveDays() + leave.getNoOfDays()));
        leave.setStatus(Constants.STATUS_REJECTED);

        emailService.sendMailToEmployee(userThatCreatedTheLeave, leave.getLeaveReason(), "Your manager: " + user.getFullName() + " rejected your leave request");
    }

    public void deleteLeave(Long id) throws Exception {
        Leave leave = findLeaveById(id);

        User user = leave.getUser();

        user.setAnnualLeaveDays((int) (user.getAnnualLeaveDays() + leave.getNoOfDays()));

        leaveRepository.delete(leave);
    }
}
