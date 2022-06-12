package com.lhind.annualleavemanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping(value = {"/login", "/"})
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/mainApp")
    public String getAllForms() {
        return "main";
    }

    @GetMapping("/access-denied")
    public String error403() {
        return "access-denied";
    }
}
