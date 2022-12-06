package com.lhind.annualleavemanagement.leave.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.mapper.LeaveMapper;
import com.lhind.annualleavemanagement.leave.mapper.LeaveMapperContext;
import com.lhind.annualleavemanagement.leave.service.LeaveService;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.mapper.UserMapper;
import com.lhind.annualleavemanagement.user.mapper.UserMapperContext;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;

@Controller
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;
    private final LeaveMapper mapper;
    private final UserMapper userMapper;

    @Autowired
    public LeaveController(LeaveService leaveService, UserService userService, LeaveMapper mapper, UserMapper userMapper) {
        this.leaveService = leaveService;
        this.userService = userService;
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/manager/manageAllEmployeeLeavesUnderManager")
    public String showAllEmployeeLeavesUnderManager(Model model) {
        List<LeaveDto> allEmployeeLeavesUnderManager = leaveService
            .findAllEmployeeLeavesUnderManager()
            .stream()
            .map(e -> mapper.toDto(e, new LeaveMapperContext()))
            .collect(Collectors.toList());

        model.addAttribute("allEmployeesLeavesUnderManager", allEmployeeLeavesUnderManager);

        return "manage-leaves";
    }

    @GetMapping("/user/createNewLeaveRequest")
    public String showFormForLeave(Model model) {
        LeaveDto leave = new LeaveDto();

        CustomUserDetails customUserDetails = CurrentAuthenticatedUser.getCurrentUser();
        UserDto user = userMapper.toDto(userService.findUserById(customUserDetails.getId()), new UserMapperContext());

        model.addAttribute("leave", leave);
        model.addAttribute("user", user);

        return "register-leave";
    }

    @PostMapping("/user/saveNewLeave")
    public String saveNewLeave(@ModelAttribute("leave") @Valid LeaveDto leave) {
        leaveService.saveLeaveToCurrentlyAuthenticatedUser(mapper.toEntity(leave, new LeaveMapperContext()));

        return "redirect:/user/manageMyLeaves";
    }

    @GetMapping("/user/showLeaveFormForUpdate")
    public String showLeaveFormForUpdate(@RequestParam("leaveId") Long id, Model model) throws Exception {
        LeaveDto leave = mapper.toDto(leaveService.findLeaveById(id), new LeaveMapperContext());

        model.addAttribute("leave", leave);

        return "update-leave";
    }

    @PostMapping("/user/updateLeave")
    public String updateLeave(@ModelAttribute("leave") @Valid LeaveDto dto, @RequestParam("leaveReason") String leaveReason,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws Exception {
        leaveService.updateLeave(dto.getLeaveId(), leaveReason, fromDate, toDate);

        return "redirect:/user/manageMyLeaves";
    }

    @PostMapping("/manager/acceptLeaveRequest")
    public String acceptLeaveRequest(@RequestParam("leaveId") Long leaveId) throws Exception {
        leaveService.acceptLeaveRequest(leaveId);

        return "redirect:/manager/manageAllEmployeeLeavesUnderManager";
    }

    @PostMapping("/manager/rejectLeaveRequest")
    public String rejectLeaveRequest(@RequestParam("leaveId") Long leaveId) throws Exception {
        leaveService.rejectLeaveRequest(leaveId);

        return "redirect:/manager/manageAllEmployeeLeavesUnderManager";
    }

    @GetMapping("/user/deleteLeave")
    public String deleteLeave(@RequestParam("leaveId") Long leaveId) throws Exception {
        leaveService.deleteLeave(leaveId);

        return "redirect:/user/manageMyLeaves";
    }
}
