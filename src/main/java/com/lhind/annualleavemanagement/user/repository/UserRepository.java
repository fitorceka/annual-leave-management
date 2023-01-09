package com.lhind.annualleavemanagement.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1")
    UserEntity findUserByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.role <> 'ADMIN'")
    List<UserEntity> findAllUsers();

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'MANAGER'")
    List<UserEntity> findAllManagers();

    @Query("SELECT u FROM UserEntity u WHERE u.role = 'EMPLOYEE' and u.manager.userId = ?1")
    List<UserEntity> findAllUsersUnderManager(Long managerId);

    @Query("SELECT u.userId FROM UserEntity u")
    List<Long> findAllIds();
}
