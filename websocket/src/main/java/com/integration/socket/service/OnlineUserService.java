package com.integration.socket.service;

import com.integration.socket.model.MessageDto;
import com.integration.socket.model.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate simpMessagingTemplate;

    public OnlineUserService(@Lazy SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

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

    public void sendJoinMessageAndStatus(String username) {
        if (sessionMap.get(username) == null) {
            return;
        }

        simpMessagingTemplate.convertAndSend(
            "/topic/send",
            new MessageDto(String.valueOf(sessionMap.size()), MessageType.USER_COUNT));

        simpMessagingTemplate.convertAndSend(
            "/topic/send",
            new MessageDto(username + "加入了!", MessageType.SYSTEM_MESSAGE));
    }

    public void removeAndSendMessageStatus(String username) {
        if (sessionMap.get(username) == null) {
            return;
        }

        remove(username);

        simpMessagingTemplate.convertAndSend(
            "/topic/send",
            new MessageDto(String.valueOf(sessionMap.size()), MessageType.USER_COUNT));

        simpMessagingTemplate.convertAndSend(
            "/topic/send",
            new MessageDto(username + "离开了!", MessageType.SYSTEM_MESSAGE));
    }
}
