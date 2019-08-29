package com.integration.provider.manager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 蒋文龙(Vin)
 * @className ScheduleManager
 * @description @scheduled @PostConstruct @PreDestroy都用protected
 * @date 2019/8/15
 */
@Component
@Slf4j
public class ScheduleManager {
    @Scheduled(cron = "*/10 * * * * ?")
    protected void schedule() {
        log.info("定时计划开启...");
    }

    @PostConstruct
    protected void init() {
        log.info("构造函数开启...");
    }

    @PreDestroy
    protected void destroy() {
        log.info("析构函数开启...");
    }
}
