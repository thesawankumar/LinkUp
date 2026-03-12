package com.chatapp.chat_backend.websocket;

import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ─── User CONNECT hua ────────────────────────────────
    @EventListener
    public void handleConnect(SessionConnectedEvent event) {

        Principal principal = event.getUser();
        if (principal == null) return;

        // User online mark karo
        userRepository.findByEmail(principal.getName())
                .ifPresent(user -> {
                    user.setOnline(true);
                    userRepository.save(user);

                    // Sabko batao yeh user online aa gaya
                    broadcastStatus(user, true);
                    log.info("{} connected!", user.getName());
                });
    }

    // ─── User DISCONNECT hua ─────────────────────────────
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        Principal principal = event.getUser();
        if (principal == null) return;

        // User offline mark karo
        userRepository.findByEmail(principal.getName())
                .ifPresent(user -> {
                    user.setOnline(false);
                    user.setLastSeen(LocalDateTime.now());
                    userRepository.save(user);

                    // Sabko batao yeh user offline ho gaya
                    broadcastStatus(user, false);
                    log.info("{} disconnected!", user.getName());
                });
    }

    // ─── Status Broadcast ────────────────────────────────
    private void broadcastStatus(User user, boolean online) {
        messagingTemplate.convertAndSend(
                "/topic/status",
                Map.of(
                        "userId", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "online", online
                )
        );
    }
}
