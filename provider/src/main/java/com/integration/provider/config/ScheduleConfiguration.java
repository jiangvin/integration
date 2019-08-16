package com.integration.provider.config;

import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author 蒋文龙(Vin)
 * @className ScheduleConfiguration
 * @description
 * @date 2019/8/15
 */

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "schedule.active", havingValue = "true")
public class ScheduleConfiguration implements SchedulingConfigurer {

    /**
     * 设定连接池，默认是1，只能支持一个schedule
     * @param taskRegistrar 入参
     */
    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(2));
    }
}
