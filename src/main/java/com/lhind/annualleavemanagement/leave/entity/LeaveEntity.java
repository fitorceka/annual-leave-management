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
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Entity
@Table(name = "LEAVES")
public class LeaveEntity {

    @Id
    @Column(name = "LEAVE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @Size(max = 20)
    @Column(name = "LEAVE_REASON", nullable = false, length = 20)
    private String leaveReason;

    @Column(name = "FROM_DATE", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @Column(name = "TO_DATE", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    @Column(name = "NUMBER_OF_DAYS")
    private int noOfDays;

    @Column(name = "STATUS")
    private String status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "USER_ID")
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

    public int getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(int noOfDays) {
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
