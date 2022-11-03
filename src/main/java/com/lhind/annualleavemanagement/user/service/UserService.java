package com.lhind.annualleavemanagement.user.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.leave.repository.LeaveRepository;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;

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
        UserEntity userEntity = userRepository.findUserByEmail(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(Constants.USER_NOT_FOUND);
        }

        return new CustomUserDetails(userEntity);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAllManagers() {
        return userRepository.findAllManagers();
    }

    @Transactional(readOnly = true)
    public UserEntity findUserById(Long id) {
        return userRepository
            .findById(id)
            .orElseThrow(
                () -> new RuntimeException(Constants.USER_CANNOT_BE_FOUND_BY_ID.replace("userId", String.valueOf(id))));
    }

    public void saveUser(UserEntity userEntity) {
        if (userEntity.getHireDate().isAfter(LocalDate.now())) {
            throw new RuntimeException(Constants.HIRE_DATE_CANNOT_BE_SET_AFTER_CURRENT_DATE);
        }

        long daysFromHire = Duration.between(userEntity.getHireDate(), LocalDate.now()).toDays();

        userEntity.setDaysFromHire(daysFromHire);

        if (daysFromHire >= 90) {
            userEntity.setAnnualLeaveDays(20);
        } else {
            userEntity.setAnnualLeaveDays(0);
        }

        userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
    }

    public void updateUser(Long userId, String firstName, String lastName, String email, String userName, String role) {
        UserEntity userEntity = findUserById(userId);

        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setUsername(userName);
        userEntity.setRole(role);

        userRepository.save(userEntity);
    }

    public void deleteUser(Long id) {
        UserEntity userEntity = findUserById(id);

        if (userEntity.getRole().equals(Constants.ROLE_MANAGER)) {
            List<UserEntity> userEntities = userRepository.findAllUsersUnderManager(userEntity.getUserId());
            for (UserEntity userEntity1 : userEntities) {
                userEntity1.setManager(null);
                userRepository.save(userEntity1);
            }
        }
        userEntity.setManager(null);
        List<LeaveEntity> leaves = userEntity.getLeaves();

        for (LeaveEntity leaveEntity : leaves) {
            leaveEntity.setUser(null);
            leaveRepository.delete(leaveEntity);
        }
        userRepository.save(userEntity);
        userRepository.delete(userEntity);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        UserEntity userEntity = findUserById(id);

        if (!bCryptPasswordEncoder.matches(oldPassword, userEntity.getPassword())) {
            throw new RuntimeException(Constants.OLD_PASSWORD_DOES_NOT_MATCH);
        }
        userEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    public void setManager(Long userId, String managerEmail) {
        UserEntity userEntity = findUserById(userId);
        UserEntity manager = userRepository.findUserByEmail(managerEmail);

        if (!manager.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.USER_NOT_MANAGER);
        }

        userEntity.setManager(manager);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAllUsersUnderCurrentManager() {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity manager = findUserById(user.getId());

        if (!manager.getRole().equals(Constants.ROLE_MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        return userRepository.findAllUsersUnderManager(manager.getUserId());
    }

    public void changePasswordForAuthenticatedUser(String oldPassword, String newPassword) {
        CustomUserDetails user = CurrentAuthenticatedUser.getCurrentUser();
        UserEntity authenticatedUserEntity = findUserById(user.getId());

        if (!bCryptPasswordEncoder.matches(oldPassword, authenticatedUserEntity.getPassword())) {
            throw new RuntimeException(Constants.OLD_PASSWORD_DOES_NOT_MATCH);
        }
        authenticatedUserEntity.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(authenticatedUserEntity);
    }
}
