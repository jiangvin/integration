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
public class SocketSessionService {
    private ConcurrentHashMap<String, String> sessionMap = new ConcurrentHashMap<>();

    public void add(String key, String sessionId) {
        log.info("add new session:{}", key);
        sessionMap.put(key, sessionId);
    }

    public void remove(String key) {
        log.info("remove session:{}", key);
        sessionMap.remove(key);
    }

    public String get(String key) {
        return sessionMap.get(key);
    }
}
