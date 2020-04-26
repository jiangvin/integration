package com.integration.socket.configuration;

import com.integration.socket.handler.PrincipalHandshakeHandler;
import com.integration.socket.factory.WebSocketDecoratorFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * @author 蒋文龙(Vin)
 * @description EnableWebSocketMessageBroker 表示使用STOMP协议来传输基于消息代理的消息，此时可以在@Controller类中使用@MessageMapping
 * @date 2020/4/23
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final WebSocketDecoratorFactory webSocketDecoratorFactory;
    private final PrincipalHandshakeHandler principalHandshakeHandler;

    public WebSocketConfiguration(WebSocketDecoratorFactory webSocketDecoratorFactory, PrincipalHandshakeHandler principalHandshakeHandler) {
        this.webSocketDecoratorFactory = webSocketDecoratorFactory;
        this.principalHandshakeHandler = principalHandshakeHandler;
    }

    /**
     * 注册 Stomp的端点
     * addEndpoint：添加STOMP协议的端点。这个HTTP URL是供WebSocket或SockJS客户端访问的地址
     * setAllowedOrigins 添加允许跨域访问
     * withSockJS：指定端点使用SockJS协议
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket-simple")
        .setAllowedOrigins("*")
        .setHandshakeHandler(principalHandshakeHandler)
        .withSockJS();
    }

    /**
     * 配置消息代理
     * 启动简单Broker，消息的发送的地址符合配置的前缀来的消息才发送到这个broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        registration.addDecoratorFactory(webSocketDecoratorFactory);
//    }
}
