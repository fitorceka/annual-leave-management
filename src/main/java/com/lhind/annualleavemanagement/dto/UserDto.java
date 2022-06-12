package com.lhind.annualleavemanagement.dto;

import java.time.LocalDate;
import java.util.List;

public class UserDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private int annualLeaveDays;
    private String role;
    private LocalDate hireDate;
    private long daysFromHire;
    private UserDto manager;
    private List<LeaveDto> leaveDtoList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAnnualLeaveDays() {
        return annualLeaveDays;
    }

    public void setAnnualLeaveDays(int annualLeaveDays) {
        this.annualLeaveDays = annualLeaveDays;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public long getDaysFromHire() {
        return daysFromHire;
    }

    public void setDaysFromHire(long daysFromHire) {
        this.daysFromHire = daysFromHire;
    }

    public UserDto getManager() {
        return manager;
    }

    public void setManager(UserDto manager) {
        this.manager = manager;
    }

    public List<LeaveDto> getLeaveDtoList() {
        return leaveDtoList;
    }

    public void setLeaveDtoList(List<LeaveDto> leaveDtoList) {
        this.leaveDtoList = leaveDtoList;
    }
}
