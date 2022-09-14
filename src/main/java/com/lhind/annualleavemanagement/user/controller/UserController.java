package com.lhind.annualleavemanagement.user.controller;

import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.mapper.LeaveMapper;
import com.lhind.annualleavemanagement.leave.service.LeaveService;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.mapper.UserMapper;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private LeaveMapper leaveMapper;

    @GetMapping("/manager/managerHome")
    public String showHomeForManagers() {
        return "manager-home";
    }

    @GetMapping("/manager/showUsersUnderManager")
    public String showUsersUnderManager(Model model) throws Exception {
        List<UserDto> usersUnderCurrentManager = mapper.toDtos(userService.findAllUsersUnderCurrentManager());

        model.addAttribute("usersUnderCurrentManager", usersUnderCurrentManager);

        return "users-under-manager";
    }

    @GetMapping("/user/userHome")
    public String showUserHome() {
        return "user-home";
    }

    @GetMapping("/user/manageMyLeaves")
    public String manageMyLeaves(Model model) throws Exception {
        CustomUserDetails currentAuthenticatedUser = CurrentAuthenticatedUser.getCurrentUser();
        UserDto user = mapper.toDto(userService.findUserById(currentAuthenticatedUser.getId()));

        List<LeaveDto> leaves = leaveMapper.toDtos(leaveService.findAllLeavesForAuthenticatedUser());

        model.addAttribute("leaves", leaves);
        model.addAttribute("currentUser", user);

        return "manage-my-leaves";
    }

    @GetMapping("/showFormForChangePassword")
    public String showPasswordForm() {
        return "change-password-user";
    }

    @PostMapping("/changePassword")
    public String changeCurrentUserPassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) throws Exception {
        userService.changePasswordForAuthenticatedUser(oldPassword, newPassword);

        return "redirect:/mainApp";
    }
}
