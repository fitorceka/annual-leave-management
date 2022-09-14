package com.lhind.annualleavemanagement.user.repository;

import com.lhind.annualleavemanagement.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
