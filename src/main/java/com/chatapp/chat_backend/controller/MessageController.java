//package com.chatapp.chat_backend.controller;
//
//import com.chatapp.chat_backend.dtos.MessageDTO;
//import com.chatapp.chat_backend.service.MessageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/messages")
//@RequiredArgsConstructor
//public class MessageController {
//
//    private final MessageService messageService;
//
//    // ─── ROOM KI MESSAGES FETCH KARO ─────────────────────
//    // GET /api/messages/1
//    @GetMapping("/{roomId}")
//    public ResponseEntity<List<MessageDTO>> getMessages(
//            @PathVariable Long roomId) {
//        return ResponseEntity.ok(
//                messageService.getMessagesByRoom(roomId)
//        );
//    }
//}



package com.chatapp.chat_backend.controller;


import com.chatapp.chat_backend.dtos.MessageDTO;
import com.chatapp.chat_backend.dtos.TypingDTO;
import com.chatapp.chat_backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/messages/{roomId}")
    public ResponseEntity<List<com.chatapp.chat_backend.dtos.MessageDTO>> getMessages(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(
                messageService.getMessagesByRoom(roomId)
        );
    }

    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload MessageDTO messageDTO,
            Principal principal) {
        MessageDTO saved = messageService.saveMessage(
                messageDTO.getContent(),
                principal.getName(),
                roomId
        );
        messagingTemplate.convertAndSend(
                "/topic/chatroom/" + roomId,
                saved
        );
    }

    @MessageMapping("/chat.typing/{roomId}")
    public void typing(
            @DestinationVariable Long roomId,
            @Payload TypingDTO typingDTO,
            Principal principal) {

        // Email se user dhundo aur naam set karo
        String email = principal.getName();
        typingDTO.setEmail(email);
        typingDTO.setRoomId(roomId);

        // Email se naam nikalo — "sawankushwaha249@gmail.com" → "Sawankushwaha249"
        String name = email.split("@")[0];
        typingDTO.setName(name);  // ← Yeh add karo

        messagingTemplate.convertAndSend(
                "/topic/chatroom/" + roomId + "/typing",
                typingDTO
        );
    }
}