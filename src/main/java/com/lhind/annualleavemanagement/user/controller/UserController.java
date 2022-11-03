package com.lhind.annualleavemanagement.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.mapper.LeaveMapper;
import com.lhind.annualleavemanagement.leave.service.LeaveService;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.mapper.UserMapper;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;

@Controller
public class UserController {

    private final UserService userService;
    private final LeaveService leaveService;
    private final UserMapper mapper;
    private final LeaveMapper leaveMapper;

    @Autowired
    public UserController(UserService userService, LeaveService leaveService, UserMapper mapper, LeaveMapper leaveMapper) {
        this.userService = userService;
        this.leaveService = leaveService;
        this.mapper = mapper;
        this.leaveMapper = leaveMapper;
    }

    @GetMapping("/manager/managerHome")
    public String showHomeForManagers() {
        return "manager-home";
    }

    @GetMapping("/manager/showUsersUnderManager")
    public String showUsersUnderManager(Model model) {
        List<UserDto> usersUnderCurrentManager = userService
            .findAllUsersUnderCurrentManager()
            .stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());

        model.addAttribute("usersUnderCurrentManager", usersUnderCurrentManager);

        return "users-under-manager";
    }

    @GetMapping("/user/userHome")
    public String showUserHome() {
        return "user-home";
    }

    @GetMapping("/user/manageMyLeaves")
    public String manageMyLeaves(Model model) {
        CustomUserDetails currentAuthenticatedUser = CurrentAuthenticatedUser.getCurrentUser();
        UserDto user = mapper.toDto(userService.findUserById(currentAuthenticatedUser.getId()));

        List<LeaveDto> leaves = leaveService
            .findAllLeavesForAuthenticatedUser()
            .stream()
            .map(leaveMapper::toDto)
            .collect(Collectors.toList());

        model.addAttribute("leaves", leaves);
        model.addAttribute("currentUser", user);

        return "manage-my-leaves";
    }

    @GetMapping("/showFormForChangePassword")
    public String showPasswordForm() {
        return "change-password-user";
    }

    @PostMapping("/changePassword")
    public String changeCurrentUserPassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        userService.changePasswordForAuthenticatedUser(oldPassword, newPassword);

        return "redirect:/mainApp";
    }
}
