package com.lhind.annualleavemanagement.repository;

import com.lhind.annualleavemanagement.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
}
