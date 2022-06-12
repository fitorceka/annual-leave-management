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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(Constants.USER_NOT_FOUND);
        }

        return new CustomUserDetails(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Transactional(readOnly = true)
    public List<User> findAllManagers() {
        return userRepository.findAllManagers();
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        else {
            throw new Exception(Constants.USER_CANNOT_BE_FOUND_BY_ID.replace("userId", String.valueOf(id)));
        }
    }

    public void saveUser(User user) {
        if (user.getHireDate().atStartOfDay().toLocalDate().isAfter(DateUtils.fetchDateAndTimeOfCurrentMachine())) {
            throw new RuntimeException(Constants.HIRE_DATE_CANNOT_BE_SET_AFTER_CURRENT_DATE);
        }

        long daysFromHire = Duration.between(user.getHireDate().atStartOfDay(), DateUtils.fetchDateOfCurrentMachine()).toDays();

        user.setDaysFromHire(daysFromHire);

        if (daysFromHire >= 90) {
            user.setAnnualLeaveDays(20);
        }
        else {
            user.setAnnualLeaveDays(0);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(Long userId, String firstName, String lastName, String email, String userName, String role) throws Exception {
        User user = findUserById(userId);


        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(userName);
        user.setRole(role);

        userRepository.save(user);
    }

    public void deleteUser(Long id) throws Exception {
        User user = findUserById(id);

        if (user.getRole().equals(Constants.ROLE_MANAGER)) {
            List<User> users = userRepository.findAllUsersUnderManager(user.getUserId());
            for (User user1 : users) {
                user1.setManager(null);
                userRepository.save(user1);
            }
        }
        user.setManager(null);
        List<Leave> leaves = user.getLeaveList();

        for (Leave leave : leaves) {
            leave.setUser(null);
            leaveRepository.delete(leave);
        }
        userRepository.save(user);
        userRepository.delete(user);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) throws Exception {
        User user = findUserById(id);

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException(Constants.OLD_PASSWORD_DOES_NOT_MATCH);
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void setManager(Long userId, String managerEmail) throws Exception {
        User user = findUserById(userId);
        User manager = userRepository.findUserByEmail(managerEmail);

        if (!manager.getRole().equals(Constants.ROLE_MANAGER)){
            throw new RuntimeException(Constants.USER_NOT_MANAGER);
        }

        user.setManager(manager);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsersUnderCurrentManager() throws Exception {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User manager = findUserById(user.getId());

        if (!manager.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        return userRepository.findAllUsersUnderManager(manager.getUserId());
    }

    public void changePasswordForAuthenticatedUser(String oldPassword, String newPassword) throws Exception {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        User authenticatedUser = findUserById(user.getId());

        if (!bCryptPasswordEncoder.matches(oldPassword, authenticatedUser.getPassword())) {
            throw new RuntimeException(Constants.OLD_PASSWORD_DOES_NOT_MATCH);
        }
        authenticatedUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(authenticatedUser);
    }
}
