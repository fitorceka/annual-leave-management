package com.lhind.annualleavemanagement.util.service;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import com.lhind.annualleavemanagement.user.repository.UserRepository;
import com.lhind.annualleavemanagement.user.service.UserService;
import com.lhind.annualleavemanagement.util.HasLogger;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataUpdateJob implements Job, HasLogger {

    private final UserRepository userRepository;
    private final UserService userService;
    private final DataUpdateAsync dataUpdateAsync;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        getLogger().debug("Started data update task");
        List<Long> userIds = userRepository.findAllIds();
        getLogger().info("Processing {} users", userIds.size());
        if (userIds.size() > 1) {
            userIds.forEach(dataUpdateAsync::dataUpdate);
        } else {
            userIds.forEach(userService::dataUpdate);
        }
        getLogger().debug("Finished data update task");
    }
}
