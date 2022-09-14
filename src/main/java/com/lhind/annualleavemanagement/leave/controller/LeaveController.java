package com.lhind.annualleavemanagement.leave.controller;

import com.lhind.annualleavemanagement.leave.dto.LeaveDto;
import com.lhind.annualleavemanagement.leave.mapper.LeaveMapper;
import com.lhind.annualleavemanagement.leave.service.LeaveService;
import com.lhind.annualleavemanagement.security.CustomUserDetails;
import com.lhind.annualleavemanagement.user.dto.UserDto;
import com.lhind.annualleavemanagement.user.mapper.UserMapper;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.CurrentAuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/manager/manageAllEmployeeLeavesUnderManager")
    public String showAllEmployeeLeavesUnderManager(Model model) throws Exception {
        List<LeaveDto> allEmployeeLeavesUnderManager = mapper.toDtos(leaveService.findAllEmployeeLeavesUnderManager());

        model.addAttribute("allEmployeesLeavesUnderManager", allEmployeeLeavesUnderManager);

        return "manage-leaves";
    }

    @GetMapping("/user/createNewLeaveRequest")
    public String showFormForLeave(Model model) {
        LeaveDto leave = new LeaveDto();

        CustomUserDetails customUserDetails = CurrentAuthenticatedUser.getCurrentUser();
        UserDto user = userMapper.toDto(userService.findUserById(customUserDetails.getId()));

        model.addAttribute("leave", leave);
        model.addAttribute("user", user);

        return "register-leave";
    }

    @PostMapping("/user/saveNewLeave")
    public String saveNewLeave(@ModelAttribute("leave") @Valid LeaveDto leave) throws Exception {
        leaveService.saveLeaveToCurrentlyAuthenticatedUser(mapper.toEntity(leave));

        return "redirect:/user/manageMyLeaves";
    }

    @GetMapping("/user/showLeaveFormForUpdate")
    public String showLeaveFormForUpdate(@RequestParam("leaveId") Long id, Model model) throws Exception {
        LeaveDto leave = mapper.toDto(leaveService.findLeaveById(id));

        model.addAttribute("leave", leave);

        return "update-leave";
    }

    @PostMapping("/user/updateLeave")
    public String updateLeave(@ModelAttribute("leave") @Valid LeaveDto dto, @RequestParam("leaveReason") String leaveReason, @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate
            , @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws Exception {
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
