package com.lhind.annualleavemanagement.leave.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Entity
@Table(name = "leaves")
public class LeaveEntity {

    @Id
    @Column(name = "leave_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @Column(name = "leave_reason", nullable = false, length = 3)
    private String leaveReason;

    @Column(name = "from_date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    @Column(name = "number_of_days")
    private Long noOfDays;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "users_id")
    private UserEntity user;

    public Long getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Long getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(Long noOfDays) {
        this.noOfDays = noOfDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
