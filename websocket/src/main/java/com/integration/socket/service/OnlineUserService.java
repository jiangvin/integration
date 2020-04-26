package com.integration.socket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 蒋文龙(Vin)
 * @description socket连接管理服务
 * @date 2020/4/23
 */

@Service
@Slf4j
public class OnlineUserService {
    private ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    public void add(String key, String sessionId) {
        sessionMap.put(key, sessionId);
        log.info("add new session:{}({})", key, sessionMap.size());
    }

    public void remove(String key) {
        sessionMap.remove(key);
        log.info("remove session:{}({})", key, sessionMap.size());
    }

    public String get(String key) {
        return sessionMap.get(key);
    }

    public int getUserCount() {
        return sessionMap.size();
    }
}
