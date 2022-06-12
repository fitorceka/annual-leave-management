package com.lhind.annualleavemanagement.repository;

import com.lhind.annualleavemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role <> 'ADMIN'")
    List<User> findAllUsers();

    @Query("SELECT u FROM User u WHERE u.role = 'MANAGER'")
    List<User> findAllManagers();

    @Query("SELECT u FROM User u WHERE u.role = 'EMPLOYEE' and u.manager.userId = ?1")
    List<User> findAllUsersUnderManager(Long managerId);
}
