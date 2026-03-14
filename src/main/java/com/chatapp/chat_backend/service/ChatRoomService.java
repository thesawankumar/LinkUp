package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.enums.RoomType;
import com.chatapp.chat_backend.repository.ChatRoomRepository;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        room.setRoomType(RoomType.GROUP);

        // Creator automatically member ban jayega
        room.getMembers().add(creator);

        return chatRoomRepository.save(room);
    }

    // Direct message room banao ya already hai toh wahi lo
    public ChatRoom getOrCreateDirectRoom(Long userId1, Long userId2) {
        // Pehle existing room check karo
        Optional<ChatRoom> existing = chatRoomRepository
                .findDirectRoom(userId1, userId2, RoomType.DIRECT);
        if (existing.isPresent()) return existing.get();

        // Dono users fetch karo
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User1 nahi mila!"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User2 nahi mila!"));

        // Naya DM room banao
        ChatRoom room = new ChatRoom();
        room.setName(user1.getName() + " & " + user2.getName());
        room.setRoomType(RoomType.DIRECT);  // ← ZAROORI
        room.setCreatedBy(user1);
        room.getMembers().add(user1);
        room.getMembers().add(user2);

        return chatRoomRepository.save(room);
    }

    // Sirf GROUP rooms lo
    public List<ChatRoom> getGroupRooms() {
        return chatRoomRepository.findAll()
                .stream()
                .filter(r -> r.getRoomType() == RoomType.GROUP)
                .collect(Collectors.toList());
    }

    // User ke saare DM rooms lo
    public List<ChatRoom> getDirectRooms(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow();
        return chatRoomRepository.findAll()
                .stream()
                .filter(r -> r.getRoomType() == RoomType.DIRECT
                        && r.getMembers().contains(user))
                .collect(Collectors.toList());
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
