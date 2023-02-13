package com.lhind.annualleavemanagement.leave.entity;

import java.time.LocalDate;

import com.lhind.annualleavemanagement.user.entity.UserEntity;
import com.lhind.annualleavemanagement.util.enums.LeaveReason;
import com.lhind.annualleavemanagement.util.enums.Status;

public final class LeaveEntityBuilder {

    private Long leaveId;
    private LeaveReason leaveReason;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int noOfDays;
    private Status status;
    private UserEntity user;

    public static LeaveEntityBuilder newBuilder() {
        return new LeaveEntityBuilder();
    }

    public LeaveEntityBuilder withLeaveId(Long id) {
        this.leaveId = id;
        return this;
    }

    public LeaveEntityBuilder withLeaveReason(LeaveReason leaveReason) {
        this.leaveReason = leaveReason;
        return this;
    }

    public LeaveEntityBuilder withFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public LeaveEntityBuilder withToDate(LocalDate toDate) {
        this.toDate = toDate;
        return this;
    }

    public LeaveEntityBuilder withNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
        return this;
    }

    public LeaveEntityBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public LeaveEntityBuilder withUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public LeaveEntity build() {
        LeaveEntity leave = new LeaveEntity();

        leave.setLeaveId(this.leaveId);
        leave.setLeaveReason(this.leaveReason);
        leave.setFromDate(this.fromDate);
        leave.setToDate(this.toDate);
        leave.setNoOfDays(this.noOfDays);
        leave.setStatus(this.status);
        leave.setUser(this.user);

        return leave;
    }
}
