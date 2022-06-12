package com.lhind.annualleavemanagement.controller;

import com.lhind.annualleavemanagement.entity.Leave;
import com.lhind.annualleavemanagement.entity.User;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.service.LeaveService;
import com.lhind.annualleavemanagement.service.UserService;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveService leaveService;

    @GetMapping("/manager/managerHome")
    public String showHomeForManagers() {
        return "manager-home";
    }

    @GetMapping("/manager/showUsersUnderManager")
    public String showUsersUnderManager(Model model) throws Exception {
        List<User> usersUnderCurrentManager = userService.findAllUsersUnderCurrentManager();

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
        User user = userService.findUserById(currentAuthenticatedUser.getId());

        List<Leave> leaves = leaveService.findAllLeavesForAuthenticatedUser();

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
