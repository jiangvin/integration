package com.integration.socket.controller;

import com.integration.socket.model.dto.MessageDto;
import com.integration.socket.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/23
 */

@Slf4j
@Controller("/")
public class MainController {

    private final MessageService messageService;

    public MainController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public ModelAndView tankGame() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        return mav;
    }

    @GetMapping("/chat")
    public ModelAndView helloWorld() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("main");
        mav.getModel().put("name", "Hello World!");
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
        if (accessor.getUser() == null) {
            return;
        }

        String username = accessor.getUser().getName();
        log.info("receive:{} from user:{}", messageDto.toString(), username);
        messageService.receiveMessage(messageDto, username);
    }
}
