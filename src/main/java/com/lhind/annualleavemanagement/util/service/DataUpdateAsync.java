package com.lhind.annualleavemanagement.util.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lhind.annualleavemanagement.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataUpdateAsync {

    private final UserService userService;

    @Async
    public void dataUpdate(Long id) {
        userService.dataUpdate(id);
    }
}
