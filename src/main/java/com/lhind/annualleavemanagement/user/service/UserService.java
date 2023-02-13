package com.lhind.annualleavemanagement.user.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.leave.repository.LeaveRepository;
import com.lhind.annualleavemanagement.security.CurrentAuthenticatedUser;
import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.util.Constants;
import com.lhind.annualleavemanagement.util.enums.Role;

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
        return userRepository
            .findUserByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(Constants.USER_NOT_FOUND));
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

        int daysFromHire = (int) Duration
            .between(userEntity.getHireDate().atStartOfDay(), LocalDate.now().atStartOfDay())
            .toDays();

        userEntity.setDaysFromHire(daysFromHire);

        if (daysFromHire >= 90) {
            userEntity.setAnnualLeaveDays(20);
        } else {
            userEntity.setAnnualLeaveDays(0);
        }

        userRepository.save(userEntity);
    }

    public void updateUser(Long userId, String firstName, String lastName, String email, String role) {
        UserEntity userEntity = findUserById(userId);

        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setRole(Role.valueOf(role));

        userRepository.save(userEntity);
    }

    public void deleteUser(Long id) {
        UserEntity userEntity = findUserById(id);

        if (userEntity.getRole() == Role.MANAGER) {
            List<UserEntity> userEntities = userRepository.findAllUsersUnderManager(userEntity.getUserId());
            userEntities.forEach(user -> {
                user.setManager(null);
                userRepository.save(user);
            });
        }
        userEntity.setManager(null);
        List<LeaveEntity> leaves = userEntity.getLeaves();

        leaves.forEach(leave -> {
            leave.setUser(null);
            leaveRepository.save(leave);
        });

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
        Optional<UserEntity> manager = userRepository.findUserByEmail(managerEmail);

        manager
            .filter(m -> m.getRole() == Role.MANAGER)
            .ifPresent(userEntity::setManager);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAllUsersUnderCurrentManager() {
        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!(user.getRole() == Role.MANAGER)) {
            throw new RuntimeException(Constants.AUTHENTICATED_USER_IS_NOT_A_MANAGER);
        }

        return userRepository.findAllUsersUnderManager(user.getUserId());
    }

    public void changePasswordForAuthenticatedUser(String oldPassword, String newPassword) {
        UserEntity user = CurrentAuthenticatedUser.getCurrentUser();

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException(Constants.OLD_PASSWORD_DOES_NOT_MATCH);
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void dataUpdate(Long id) {
        UserEntity user = findUserById(id);

        int daysFromHire = (int) Duration
            .between(user.getHireDate().atStartOfDay(), LocalDate.now().atStartOfDay())
            .toDays();

        if (user.getDaysFromHire() != daysFromHire) {
            user.setDaysFromHire(daysFromHire);

            if (daysFromHire == 90) {
                user.setAnnualLeaveDays(20);
            }

            userRepository.save(user);
        }
    }
}
