package com.lhind.annualleavemanagement.controller;

import com.lhind.annualleavemanagement.model.User;
import com.lhind.annualleavemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    public String getAllUsers(Model model) {
        List<User> users = userService.findAllUsers();

        model.addAttribute("allUsers", users);

        return "manage-users";
    }

    @GetMapping ("/admin/registerUser")
    public String addUser(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        return "register-user";
    }

    @PostMapping ("/admin/saveUser")
    public String saveUser(@ModelAttribute("user") @Valid User user) {
        userService.saveUser(user);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/showUserFormForUpdate")
    public String showUserFormForUpdate(@RequestParam("userId") Long id, Model model) throws Exception {
        User user = userService.findUserById(id);

        model.addAttribute("user", user);

        return "update-user";
    }

    @PostMapping("/admin/updateUser")
    public String updateUser(@ModelAttribute("user") @Valid User user, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName
            , @RequestParam("email") String email, @RequestParam("username") String username
            , @RequestParam("role") String role) throws Exception {
        userService.updateUser(user.getUserId(), firstName, lastName, email, username, role);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") Long id) throws Exception {
        userService.deleteUser(id);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/showUserFormForUpdatePassword")
    public String showUserFormForUpdatePassword(@RequestParam("userId") Long id, Model model) throws Exception {
        User user = userService.findUserById(id);

        model.addAttribute("user", user);

        return "change-password";
    }

    @PostMapping("/admin/changePassword")
    public String changePassword(@ModelAttribute("user") @Valid User user, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, BindingResult bindingResult) throws Exception {
        userService.changePassword(user.getUserId(), oldPassword, newPassword);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/showSelectManager")
    public String showSelectManager(@RequestParam("userId") Long userId, Model model) throws Exception {
        User user = userService.findUserById(userId);

        List<User> managers = userService.findAllManagers();

        model.addAttribute("user", user);
        model.addAttribute("managers", managers);

        return "select-manager";
    }

    @PostMapping("/admin/setManager")
    public String setManagerForUser(@ModelAttribute("user") @Valid User user, @RequestParam("managerEmail") String managerEmail) throws Exception {
        userService.setManager(user.getUserId(), managerEmail);

        return "redirect:/admin/users";
    }
}
