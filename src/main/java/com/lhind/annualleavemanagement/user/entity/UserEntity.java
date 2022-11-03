package com.lhind.annualleavemanagement.user.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long userId;

    @Column(name = "first_name", nullable = false)
    @Size(min = 3, max = 15)
    private String firstName;

    @Size(min = 3, max = 15)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true)
    @Email(message = "Email cannot be empty")
    private String email;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "annual_leave_days", nullable = false)
    private int annualLeaveDays;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "hire_date", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hireDate;

    @Column(name = "days_from_hire", nullable = false)
    private long daysFromHire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    private UserEntity manager;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<LeaveEntity> leaves;

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

    public UserEntity getManager() {
        return manager;
    }

    public void setManager(UserEntity manager) {
        this.manager = manager;
    }

    public List<LeaveEntity> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<LeaveEntity> leaves) {
        this.leaves = leaves;
    }
}
