package com.lhind.annualleavemanagement.user.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lhind.annualleavemanagement.leave.entity.LeaveEntity;
import com.lhind.annualleavemanagement.util.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "FIRST_NAME", nullable = false)
    @Size(min = 3, max = 15)
    private String firstName;

    @Size(min = 3, max = 15)
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EMAIL", unique = true, nullable = false)
    @Email(message = "Email cannot be empty")
    private String email;

    @Size(min = 8)
    @Column(name = "PASSWORD", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "ANNUAL_LEAVE_DAYS", nullable = false)
    private int annualLeaveDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private Role role;

    @Column(name = "HIRE_DATE", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hireDate;

    @Column(name = "DAYS_FROM_HIRE", nullable = false)
    private int daysFromHire;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MANAGER_ID")
    private UserEntity manager;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<LeaveEntity> leaves;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
