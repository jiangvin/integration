package com.integration.socket.controller;

import com.integration.socket.model.MessageDto;
import com.integration.socket.service.MessageService;
import com.integration.socket.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/23
 */

@Slf4j
@Controller("/")
public class MainController {


    private final OnlineUserService onlineUserService;
    private final MessageService messageService;

    public MainController(MessageService messageService, OnlineUserService onlineUserService) {
        this.messageService = messageService;
        this.onlineUserService = onlineUserService;
    }

    @GetMapping("/")
    public ModelAndView helloWorld() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("main");
        mav.getModel().put("name", "Hello World!");
        return mav;
    }

    @GetMapping("/users")
    @ResponseBody
    public List<String> getUsers() {
        log.info("request users");
        return onlineUserService.getUserList();
    }

    @GetMapping("/tank")
    public ModelAndView fruitGame() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        return mav;
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
