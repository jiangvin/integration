package com.integration.socket.interceptor;

import com.integration.socket.model.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/26
 */

@Component
@Slf4j
public class MessageInterceptor implements ChannelInterceptor {

    @Autowired
    @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;

//    @Override
//    public boolean preReceive(MessageChannel channel) {
//        System.out.println(this.getClass().getCanonicalName() + " preReceive");
//        return super.preReceive(channel);
//    }

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        System.out.println(this.getClass().getCanonicalName() + " preSend");
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompCommand command = accessor.getCommand();
//        //检测用户订阅内容（防止用户订阅不合法频道）
//        if (StompCommand.SUBSCRIBE.equals(command)) {
//            System.out.println(this.getClass().getCanonicalName() + " 用户订阅目的地=" + accessor.getDestination());
//            // 如果该用户订阅的频道不合法直接返回null前端用户就接受不到该频道信息
//            return super.preSend(message, channel);
//        } else {
//            return super.preSend(message, channel);
//        }
//
//    }
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        System.out.println(this.getClass().getCanonicalName() + " afterSendCompletion");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (StompCommand.SUBSCRIBE.equals(command)) {
            System.out.println(this.getClass().getCanonicalName() + " 订阅消息发送成功");
            this.simpMessagingTemplate.convertAndSend("/topic/sendStatus", new MessageDto("消息发送成功"));
        }
        //如果用户断开连接
        if (StompCommand.DISCONNECT.equals(command)) {
            System.out.println(this.getClass().getCanonicalName() + "用户断开连接成功");
            simpMessagingTemplate.convertAndSend("/topic/sendStatus", new MessageDto("用户断开连接成功"));
        }
    }
}
