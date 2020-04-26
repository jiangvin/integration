package com.integration.socket.service;

import com.integration.socket.model.MessageDto;
import com.integration.socket.model.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author 蒋文龙(Vin)
 * @description 懒加载模式，避免循环依赖
 * @date 2020/4/26
 */

@Service
@Slf4j
public class MessageService {

    public static final String TOPIC_PATH = "/topic/send";

    public static final String QUEUE_PATH = "queue/send";

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void receiveMessage(MessageDto messageDto, String user) {
        messageDto.setMessage(user + ":" + messageDto.getMessage());
        sendMessage(messageDto);
    }

    public void sendMessage(MessageDto messageDto) {
        if (StringUtils.isEmpty(messageDto.getSendTo())) {
            simpMessagingTemplate.convertAndSend(
                TOPIC_PATH,
                messageDto);
        } else {
            simpMessagingTemplate.convertAndSendToUser(
                messageDto.getSendTo(),
                QUEUE_PATH,
                messageDto);
        }
    }

    public void sendUserStatusAndMessage(int userCount, String username, boolean isLeave) {
        //没人了，不用更新状态
        if (userCount == 0) {
            log.info("no user in service, no need to send message");
            return;
        }

        sendMessage(new MessageDto(String.valueOf(userCount), MessageType.USER_COUNT));
        if (isLeave) {
            sendMessage(new MessageDto(username + "离开了!", MessageType.SYSTEM_MESSAGE));
        } else {
            sendMessage(new MessageDto(username + "加入了!", MessageType.SYSTEM_MESSAGE));
        }
    }
}
