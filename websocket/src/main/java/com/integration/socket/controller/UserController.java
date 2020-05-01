package com.integration.socket.controller;

import com.integration.socket.model.dto.MessageDto;
import com.integration.socket.service.MessageService;
import com.integration.socket.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/5/1
 */

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    private final MessageService messageService;
    private final OnlineUserService onlineUserService;

    public UserController(OnlineUserService onlineUserService, MessageService messageService) {
        this.onlineUserService = onlineUserService;
        this.messageService = messageService;
    }

    @GetMapping("/getAll")
    public List<String> getUsers() {
        return onlineUserService.getUserList();
    }

    /**
     * MessageMapping：指定要接收消息的地址，类似@RequestMapping
     * SendTo: 默认消息将被发送到与传入消息相同的目的地，但是目的地前面附加前缀（默认情况下为“/topic”}
     * @param messageDto
     * @return
     */
    @MessageMapping("/send")
    public void connect(MessageDto messageDto, SimpMessageHeaderAccessor accessor) {
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        log.info("receive:{} from user:{}", messageDto.toString(), username);
        messageService.receiveMessage(messageDto, username);
    }
}
