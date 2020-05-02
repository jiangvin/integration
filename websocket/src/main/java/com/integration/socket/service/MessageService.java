package com.integration.socket.service;

import com.integration.socket.model.MessageType;
import com.integration.socket.model.dto.MessageDto;
import com.integration.socket.model.dto.TankDto;
import com.integration.util.object.ObjectUtil;
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

    public static final String QUEUE_PATH = "/queue/send";

    private final TankService tankService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageService(SimpMessagingTemplate simpMessagingTemplate, TankService tankService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.tankService = tankService;
    }

    public void sendMessage(MessageDto messageDto) {
        sendMessage(messageDto, null);
    }

    public void sendMessage(MessageDto messageDto, String sendFrom) {
        log.info("send message:{} from:{}", messageDto.toString(), sendFrom);

        String sendTo = messageDto.getSendTo();
        if (StringUtils.isEmpty(sendTo)) {
            //发送给所有人
            simpMessagingTemplate.convertAndSend(
                TOPIC_PATH,
                messageDto);
        } else {
            //发送给指定用户
            simpMessagingTemplate.convertAndSendToUser(
                sendTo,
                QUEUE_PATH,
                messageDto);

            //补发给发送者一份
            if (StringUtils.isEmpty(sendFrom) || sendFrom.equals(sendTo)) {
                return;
            }
            simpMessagingTemplate.convertAndSendToUser(
                sendFrom,
                QUEUE_PATH,
                messageDto);
        }
    }

    public void receiveMessage(MessageDto messageDto, String sendFrom) {
        switch (messageDto.getMessageType()) {
            case USER_MESSAGE:
                processUserMessage(messageDto, sendFrom);
                break;
            case ADD_TANK:
                processAddTank(messageDto, sendFrom);
                break;
            default:
                log.warn("unsupported messageType:{} from {}", messageDto.getMessageType(), sendFrom);
                break;
        }
    }

    private void processUserMessage(MessageDto messageDto, String sendFrom) {
        if (StringUtils.isEmpty(messageDto.getSendTo())) {
            messageDto.setMessage(String.format("%s: %s", sendFrom, messageDto.getMessage()));
        } else {
            messageDto.setMessage(String.format("%s → %s: %s", sendFrom, messageDto.getSendTo(), messageDto.getMessage()));
        }
        sendMessage(messageDto);
    }

    private void processAddTank(MessageDto messageDto, String sendFrom) {
        TankDto tankDto = ObjectUtil.readValue(messageDto.getMessage(), TankDto.class);
        if (tankDto == null) {
            return;
        }
        tankDto.setId(sendFrom);
        if (!tankService.addTank(tankDto)) {
            return;
        }

        //收到单位，即将向所有人同步单位信息
        MessageDto sendBack = new MessageDto(tankService.getTankList(), MessageType.TANKS);
        sendMessage(sendBack);
    }


    public void sendUserStatusAndMessage(List<String> users, String username, boolean isLeave) {
        //没人了，不用更新状态
        if (users.isEmpty()) {
            log.info("no user in service, no need to send message");
            return;
        }

        sendMessage(new MessageDto(users, MessageType.USERS));
        if (isLeave) {
            sendMessage(new MessageDto(String.format("%s 离开了! 当前人数: %d",
                                                     username,
                                                     users.size()),
                                       MessageType.SYSTEM_MESSAGE));
        } else {
            sendMessage(new MessageDto(String.format("%s 加入了! 当前人数: %d",
                                                     username,
                                                     users.size()),
                                       MessageType.SYSTEM_MESSAGE));
        }
    }
}
