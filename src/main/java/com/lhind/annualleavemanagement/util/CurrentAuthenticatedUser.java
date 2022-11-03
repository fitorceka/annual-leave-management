package com.lhind.annualleavemanagement.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lhind.annualleavemanagement.security.CustomUserDetails;

public class CurrentAuthenticatedUser {

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }
}
