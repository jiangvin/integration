package com.integration.provider.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 蒋文龙(Vin)
 * @className ScheduleManager
 * @description
 * @date 2019/8/15
 */
@Component
@Slf4j
public class ScheduleManager {
    @Scheduled(cron = "*/10 * * * * ?")
    void schedule() {
        log.info("定时计划开启...");
    }
}
