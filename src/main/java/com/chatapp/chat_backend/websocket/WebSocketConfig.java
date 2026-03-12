package com.chatapp.chat_backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   // WebSocket + STOMP enable karo
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Frontend yahan connect karega:
        // ws://localhost:8080/ws
        registry.addEndpoint("/ws")

                // React dev server ko allow karo
//                .setAllowedOrigins("http://localhost:5173")
                .setAllowedOriginPatterns("*")

                // SockJS = fallback for old browsers
                // WebSocket support nahi hai toh SockJS use karega
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // CLIENT → SERVER prefix
        // Frontend /app/chat.sendMessage bhejega
        // Spring @MessageMapping("/chat.sendMessage") handle karega
        registry.setApplicationDestinationPrefixes("/app");

        // SERVER → CLIENT prefix
        // Server /topic/chatroom/1 pe broadcast karega
        // Frontend /topic/chatroom/1 subscribe karega
        registry.enableSimpleBroker("/topic");
    }
}