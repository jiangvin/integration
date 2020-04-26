package com.integration.socket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.socket.model.MessageDto;
import com.integration.socket.model.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void receiveMessage(MessageDto messageDto, String sendFrom) {
        if (StringUtils.isEmpty(messageDto.getSendTo())) {
            messageDto.setMessage(String.format("%s: %s", sendFrom, messageDto.getMessage()));
            sendMessage(messageDto);
        } else {
            messageDto.setMessage(String.format("%s->%s: %s", sendFrom, messageDto.getSendTo(), messageDto.getMessage()));
            sendMessageToUser(messageDto, sendFrom);
        }
    }

    public void sendMessage(MessageDto messageDto) {
        log.info("send message:{}", messageDto.toString());
        simpMessagingTemplate.convertAndSend(
            TOPIC_PATH,
            messageDto);
    }

    public void sendMessageToUser(MessageDto messageDto, String sendFrom) {
        //推给发送方
        log.info("send message:{} to {}", messageDto.toString(), sendFrom);
        simpMessagingTemplate.convertAndSendToUser(
            sendFrom,
            QUEUE_PATH,
            messageDto);

        if (sendFrom.equals(messageDto.getSendTo())) {
            return;
        }

        //推给接受方
        log.info("send message:{} to {}", messageDto.toString(), messageDto.getSendTo());
        simpMessagingTemplate.convertAndSendToUser(
            messageDto.getSendTo(),
            QUEUE_PATH,
            messageDto);
    }

    public void sendUserStatusAndMessage(List<String> users, String username, boolean isLeave) throws JsonProcessingException {
        //没人了，不用更新状态
        if (users.isEmpty()) {
            log.info("no user in service, no need to send message");
            return;
        }

        sendMessage(new MessageDto(objectMapper.writeValueAsString(users), MessageType.USER_COUNT));
        if (isLeave) {
            sendMessage(new MessageDto(username + "离开了!", MessageType.SYSTEM_MESSAGE));
        } else {
            sendMessage(new MessageDto(username + "加入了!", MessageType.SYSTEM_MESSAGE));
        }
    }
}
