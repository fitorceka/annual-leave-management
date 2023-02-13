package com.lhind.annualleavemanagement.user.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lhind.annualleavemanagement.leave.dto.LeaveDto;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UserDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private int annualLeaveDays;
    private String role;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hireDate;
    private int daysFromHire;
    private UserDto manager;
    private List<LeaveDto> leaves;
}
