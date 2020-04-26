package com.integration.socket.interceptor;

import com.integration.socket.model.MessageDto;
import com.integration.socket.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/26
 */

@Component
@Slf4j
public class MessageInterceptor implements ChannelInterceptor {

    private static final String TOPIC_PATH = "/topic/send";

    private final OnlineUserService onlineUserService;

    public MessageInterceptor(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            //有新用户加入
            log.info("user:{} connected successfully!", username);
            onlineUserService.add(username, accessor.getSessionId());
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            //新用户订阅了消息
            String destination = accessor.getDestination();
            log.info("user:{} subscribe the path:{}", username, destination);
            if (TOPIC_PATH.equals(destination)) {
                onlineUserService.sendJoinMessageAndStatus(username);
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            log.info("user:{} disconnected successfully!", username);
            onlineUserService.removeAndSendMessageStatus(username);
        } else {
            log.info("user:{} send nonsupport command:{}", username, command);
        }
    }
}
