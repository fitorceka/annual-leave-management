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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "LEAVES")
@Getter
@Setter
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
}
