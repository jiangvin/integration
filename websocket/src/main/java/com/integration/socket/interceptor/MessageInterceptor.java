package com.integration.socket.interceptor;

import com.integration.socket.model.bo.UserBo;
import com.integration.socket.service.GameService;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/26
 */

@Component
@Slf4j
public class MessageInterceptor implements ChannelInterceptor {

    private final GameService gameService;

    /**
     * 缓存新用户，当用户完成所有路径订阅后才放入game中
     */
    private ConcurrentHashMap <String, UserBo> newUserCache = new ConcurrentHashMap<>();

    public MessageInterceptor(@Lazy GameService gameService) {
        this.gameService = gameService;
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
            if (newUserCache.containsKey(username)) {
                return;
            }
            newUserCache.put(username, new UserBo(username, accessor.getSessionId()));
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            //新用户订阅了消息
            if (!newUserCache.containsKey(username)) {
                return;
            }

            UserBo userBo = newUserCache.get(username);
            String destination = accessor.getDestination();
            log.info("user:{} try to subscribe the path:{}...", username, destination);
            userBo.getSubscribeList().add(destination);

            //新用户完成了订阅，从缓存中删除，并加入到游戏中
            if (userBo.isFinishSubscribe()) {
                newUserCache.remove(userBo.getUsername());
                log.info("add user:{} into game...", userBo.getUsername());
                gameService.addUser(userBo);
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            log.info("user:{} try to disconnected...", username);
            gameService.removeUser(username);
        } else if (!StompCommand.SEND.equals(command)) {
            //send类型在controller里面单独处理
            log.info("user:{} send nonsupport command:{}...", username, command);
        }
    }
}
