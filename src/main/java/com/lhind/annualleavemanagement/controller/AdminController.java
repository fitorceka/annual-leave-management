package com.lhind.annualleavemanagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.mapper.UserMapper;
import com.lhind.annualleavemanagement.user.mapper.UserMapperContext;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.Constants;

@Controller
@PreAuthorize(Constants.ROLE_ADMIN)
public class AdminController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AdminController(UserService userService, @Lazy UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/admin/users")
    public String getAllUsers(Model model) {
        List<UserDto> allUsers = userService
            .findAllUsers()
            .stream()
            .map(entity -> userMapper.toDto(entity, new UserMapperContext()))
            .collect(Collectors.toList());

        model.addAttribute("allUsers", allUsers);

        return "manage-users";
    }

    @GetMapping("/admin/registerUser")
    public String addUser(Model model) {
        UserDto user = new UserDto();

        model.addAttribute("user", user);

        return "register-user";
    }

    @PostMapping("/admin/saveUser")
    public String saveUser(@ModelAttribute("user") @Valid UserDto dto) {
        userService.saveUser(userMapper.toEntity(dto, new UserMapperContext()));

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/showUserFormForUpdate")
    public String showUserFormForUpdate(@RequestParam("userId") Long id, Model model) {
        UserDto user = userMapper.toDto(userService.findUserById(id), new UserMapperContext());

        model.addAttribute("user", user);

        return "update-user";
    }

    @PostMapping("/admin/updateUser")
    public String updateUser(@ModelAttribute("user") @Valid UserDto user, @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, @RequestParam("email") String email,
            @RequestParam("username") String username, @RequestParam("role") String role) {
        userService.updateUser(user.getUserId(), firstName, lastName, email, username, role);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") Long id) {
        userService.deleteUser(id);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/showUserFormForUpdatePassword")
    public String showUserFormForUpdatePassword(@RequestParam("userId") Long id, Model model) {
        UserDto user = userMapper.toDto(userService.findUserById(id), new UserMapperContext());

        model.addAttribute("user", user);

        return "change-password";
    }

    @PostMapping("/admin/changePassword")
    public String changePassword(@ModelAttribute("user") @Valid UserDto user, @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        userService.changePassword(user.getUserId(), oldPassword, newPassword);

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/showSelectManager")
    public String showSelectManager(@RequestParam("userId") Long userId, Model model) {
        UserDto user = userMapper.toDto(userService.findUserById(userId), new UserMapperContext());

        List<UserDto> managers = userService
            .findAllManagers()
            .stream()
            .map(entity -> userMapper.toDto(entity, new UserMapperContext()))
            .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("managers", managers);

        return "select-manager";
    }

    @PostMapping("/admin/setManager")
    public String setManagerForUser(@ModelAttribute("user") @Valid UserDto user,
            @RequestParam("managerEmail") String managerEmail) {
        userService.setManager(user.getUserId(), managerEmail);

        return "redirect:/admin/users";
    }
}
