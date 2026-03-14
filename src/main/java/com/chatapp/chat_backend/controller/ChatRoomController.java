package com.chatapp.chat_backend.controller;


import com.chatapp.chat_backend.entity.ChatRoom;
import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

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

    // GET /api/rooms/group — sirf group rooms
    @GetMapping("/group")
    public ResponseEntity<List<ChatRoom>> getGroupRooms() {
        return ResponseEntity.ok(chatRoomService.getGroupRooms());
    }

    // GET /api/rooms/direct — mera DM rooms
    @GetMapping("/direct")
    public ResponseEntity<List<ChatRoom>> getDirectRooms(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                chatRoomService.getDirectRooms(userDetails.getUsername())
        );
    }

    // POST /api/rooms/direct/{userId}
    @PostMapping("/direct/{targetUserId}")
    public ResponseEntity<ChatRoom> getOrCreateDirectRoom(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User nahi mila!"));

        ChatRoom room = chatRoomService
                .getOrCreateDirectRoom(currentUser.getId(), targetUserId);

        return ResponseEntity.ok(room);
    }


    // GET /api/rooms/{roomId}/members
    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<User>> getRoomMembers(
            @PathVariable Long roomId) {
        ChatRoom room = chatRoomService.getRoomById(roomId);
        return ResponseEntity.ok(new ArrayList<>(room.getMembers()));
    }

    // POST /api/rooms/{roomId}/invite/{userId}
    @PostMapping("/{roomId}/invite/{userId}")
    public ResponseEntity<ChatRoom> inviteMember(
            @PathVariable Long roomId,
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User nahi mila!"));

        ChatRoom room = chatRoomService.joinRoom(roomId, user.getEmail());
        return ResponseEntity.ok(room);
    }


}
