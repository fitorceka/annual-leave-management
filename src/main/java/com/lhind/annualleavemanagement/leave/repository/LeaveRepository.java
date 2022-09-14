package com.lhind.annualleavemanagement.leave.repository;

import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveEntity, Long> {
}
