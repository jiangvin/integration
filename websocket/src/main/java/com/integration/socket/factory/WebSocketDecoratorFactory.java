package com.integration.socket.factory;

import com.integration.socket.service.SocketSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.security.Principal;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/23
 */
@Component
@Slf4j
public class WebSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private final SocketSessionService socketSessionService;

    public WebSocketDecoratorFactory(SocketSessionService socketSessionService) {
        this.socketSessionService = socketSessionService;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    // 身份校验成功，缓存socket连接
                    socketSessionService.add(principal.getName(), session);
                }
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                Principal principal = session.getPrincipal();
                if (principal != null) {
                    // 身份校验成功，移除socket连接
                    socketSessionService.remove(principal.getName());
                }
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}
