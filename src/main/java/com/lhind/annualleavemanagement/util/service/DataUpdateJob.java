package com.lhind.annualleavemanagement.util.service;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.HasLogger;

@Component
public class DataUpdateJob implements Job, HasLogger {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        getLogger().debug("Started data update task");
        List<Long> userIds = userRepository.findAllIds();
        getLogger().info("Processing {} users", userIds.size());
        userIds.forEach(id -> userService.dataUpdate(id));
        getLogger().debug("Finished data update task");
    }
}
