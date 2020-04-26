package com.integration.socket.service;

import com.integration.socket.model.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 蒋文龙(Vin)
 * @description socket连接管理服务
 * @date 2020/4/23
 */

@Service
@Slf4j
public class SocketSessionService {
    private ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;

    public void add(String key, WebSocketSession webSocketSession) {
        log.info("add new session:{}", key);
        sessionMap.put(key, webSocketSession);
    }

    public void remove(String key) {
        log.info("remove session:{}", key);
        sessionMap.remove(key);
    }

    public WebSocketSession get(String key) {
        return sessionMap.get(key);
    }

    public void sendStatusToAll() {
        simpMessagingTemplate.convertAndSend(
            "/topic/sendStatus",
            new MessageDto(String.valueOf(sessionMap.size())));
    }
}
