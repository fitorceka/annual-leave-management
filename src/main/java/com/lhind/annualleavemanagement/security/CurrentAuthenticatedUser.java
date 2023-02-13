package com.lhind.annualleavemanagement.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lhind.annualleavemanagement.user.entity.UserEntity;

@Component
public class CurrentAuthenticatedUser {

    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }
}
