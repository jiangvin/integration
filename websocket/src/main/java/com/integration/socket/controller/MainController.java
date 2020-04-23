package com.integration.socket.controller;

import com.integration.socket.model.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/23
 */

@Slf4j
@Controller("/")
public class MainController {

    /**
     * 收到消息的计数
     */
    private AtomicInteger count = new AtomicInteger(0);

    @GetMapping("/")
    public ModelAndView helloWorld() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("main");
        mav.getModel().put("name", "Hello Thymeleaf!");
        return mav;
    }

    /**
     * MessageMapping：指定要接收消息的地址，类似@RequestMapping
     * SendTo: 默认消息将被发送到与传入消息相同的目的地，但是目的地前面附加前缀（默认情况下为“/topic”}
     * @param messageDto
     * @return
     */
    @MessageMapping("/send")
    public MessageDto connect(MessageDto messageDto) {
        log.info("receive:{}", messageDto.toString());
        return new MessageDto("receive [" + count.incrementAndGet() + "] records");
    }

    @GetMapping("/mock")
    @SendTo("/topic/receive")
    public MessageDto mock() {
        return new MessageDto("receive [" + count.incrementAndGet() + "] records");
    }
}
