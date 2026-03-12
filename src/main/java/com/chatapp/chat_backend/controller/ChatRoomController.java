package com.chatapp.chat_backend.controller;


import com.chatapp.chat_backend.entity.ChatRoom;
import com.chatapp.chat_backend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // ─── SABHI ROOMS LO ──────────────────────────────────
    // GET /api/rooms
    @GetMapping
    public ResponseEntity<List<ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }

    // ─── ROOM BANAO ──────────────────────────────────────
    // POST /api/rooms
    @PostMapping
    public ResponseEntity<ChatRoom> createRoom(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        // userDetails.getUsername() = logged in user ki email
        ChatRoom room = chatRoomService.createRoom(
                body.get("name"),
                body.get("description"),
                userDetails.getUsername()
        );
        return ResponseEntity.ok(room);
    }
    // ─── ROOM JOIN KARO ──────────────────────────────────
    // POST /api/rooms/1/join
    @PostMapping("/{roomId}/join")
    public ResponseEntity<ChatRoom> joinRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        ChatRoom room = chatRoomService.joinRoom(
                roomId,
                userDetails.getUsername()
        );
        return ResponseEntity.ok(room);
    }
}
