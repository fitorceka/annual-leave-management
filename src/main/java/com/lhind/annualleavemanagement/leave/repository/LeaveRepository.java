package com.lhind.annualleavemanagement.leave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveEntity, Long> {
}
