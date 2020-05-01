package com.integration.socket.interceptor;

import com.integration.socket.model.bo.UserBo;
import com.integration.socket.service.MessageService;
import com.integration.socket.service.OnlineUserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/26
 */

@Component
@Slf4j
public class MessageInterceptor implements ChannelInterceptor {

    private final OnlineUserService onlineUserService;

    private final MessageService messageService;

    public MessageInterceptor(OnlineUserService onlineUserService,
                              @Lazy MessageService messageService) {
        this.onlineUserService = onlineUserService;
        this.messageService = messageService;
    }

    @SneakyThrows
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        Principal principal = accessor.getUser();
        if (principal == null || principal.getName() == null) {
            return;
        }

        String username = principal.getName();
        if (StompCommand.CONNECT.equals(command)) {

            //有新用户加入
            log.info("user:{} try to connect...", username);
            onlineUserService.add(username, accessor.getSessionId());
        } else if (StompCommand.SUBSCRIBE.equals(command)) {

            //新用户订阅了消息
            if (onlineUserService.get(username) == null) {
                return;
            }

            UserBo userBo = onlineUserService.get(username);
            String destination = accessor.getDestination();
            log.info("user:{} try to subscribe the path:{}...", username, destination);
            userBo.getSubscribeList().add(destination);

            //新用户订阅了公共消息，这时候发送公共推送，确保新用户也能收到
            if (userBo.isFinishSubscribe()) {
                messageService.sendUserStatusAndMessage(onlineUserService.getUserList(), username, false);
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {

            //用户离开
            if (!onlineUserService.remove(username)) {
                return;
            }

            log.info("user:{} try to disconnected...", username);
            messageService.sendUserStatusAndMessage(onlineUserService.getUserList(), username, true);
        } else {
            log.info("user:{} send nonsupport command:{}...", username, command);
        }
    }
}
