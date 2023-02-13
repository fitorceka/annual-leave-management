package com.lhind.annualleavemanagement.config;

import java.time.Duration;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.lhind.annualleavemanagement.util.service.DataUpdateJob;

@Configuration
public class DataUpdateConfig {

    private final static String SCHEDULER_IDENTITY = "DataUpdateJob";

    @Bean
    public JobDetail dataUpdateTaskSchedulerJobDetails() {
        return JobBuilder
            .newJob(DataUpdateJob.class)
            .withIdentity(SCHEDULER_IDENTITY)
            .storeDurably()
            .build();
    }

    @Bean
    public SimpleTriggerFactoryBean taskSchedulerJobTrigger(JobDetail jobDetail) {
        return setupTrigger(jobDetail, "Starting Data Update Scheduler Job");
    }

    private SimpleTriggerFactoryBean setupTrigger(JobDetail jobDetail, String description) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setDescription(description);
        trigger.setRepeatInterval(Duration.ofHours(24).toMillis());
        trigger.setStartDelay(0);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }
}
