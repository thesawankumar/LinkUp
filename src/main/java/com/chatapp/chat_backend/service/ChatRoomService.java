package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.repository.ChatRoomRepository;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;


    // ─── ROOM BANAO ──────────────────────────────────────
    public ChatRoom createRoom(String name, String description,
                               String creatorEmail) {

        // Creator user dhundo
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() ->
                        new RuntimeException("User nahi mila!")
                );

        ChatRoom room = new ChatRoom();
        room.setName(name);
        room.setDescription(description);
        room.setCreatedBy(creator);

        // Creator automatically member ban jayega
        room.getMembers().add(creator);

        return chatRoomRepository.save(room);
    }

    // ─── ROOM JOIN KARO ──────────────────────────────────
    public ChatRoom joinRoom(Long roomId, String userEmail) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room nahi mili!")
                );

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User nahi mila!")
                );

        // Already member hai?
        if (room.getMembers().contains(user)) {
            return room; // Kuch mat karo, waise hi return karo
        }

        room.getMembers().add(user);
        return chatRoomRepository.save(room);
    }
    // ─── SABHI ROOMS LO ──────────────────────────────────
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    // ─── EK ROOM LO ID SE ────────────────────────────────
    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room nahi mili!")
                );
    }
}
