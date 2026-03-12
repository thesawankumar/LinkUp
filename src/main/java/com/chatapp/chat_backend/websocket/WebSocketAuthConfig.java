package com.chatapp.chat_backend.websocket;

import com.chatapp.chat_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.chatapp.chat_backend.security.CustomUserDetailsService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        // Har WebSocket message ko intercept karo
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor = MessageHeaderAccessor
                        .getAccessor(message, StompHeaderAccessor.class);

                // Sirf CONNECT event pe token check karo
                // Pehla message hota hai yeh — yahan authenticate karo
                if (accessor != null &&
                        StompCommand.CONNECT.equals(accessor.getCommand())) {

                    // Header se token nikalo
                    // Frontend connect karte waqt yeh bhejega:
                    // headers: { Authorization: "Bearer eyJ..." }
                    List<String> authHeaders = accessor
                            .getNativeHeader("Authorization");

                    if (authHeaders != null && !authHeaders.isEmpty()) {
                        String authHeader = authHeaders.get(0);

                        if (authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);

                            // Token valid hai?
                            if (jwtUtil.validateToken(token)) {
                                String email = jwtUtil.extractEmail(token);

                                // User load karo
                                UserDetails userDetails = userDetailsService
                                        .loadUserByUsername(email);

                                // Authentication set karo
                                // Ab Principal.getName() = email
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities()
                                        );

                                // WebSocket session mein user set karo
                                accessor.setUser(auth);
                            }
                        }
                    }
                }

                return message;
            }
        });
    }
}