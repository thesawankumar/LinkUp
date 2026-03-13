package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.dtos.MessageDTO;
import com.chatapp.chat_backend.entity.ChatRoom;
import com.chatapp.chat_backend.entity.Message;
import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.enums.MessageType;
import com.chatapp.chat_backend.repository.ChatRoomRepository;
import com.chatapp.chat_backend.repository.MessageRepository;
import com.chatapp.chat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    // ─── MESSAGE SAVE KARO ───────────────────────────────
    public MessageDTO saveMessage(String content, String senderEmail,
                                  Long roomId) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() ->
                        new RuntimeException("Sender nahi mila!")
                );

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room nahi mili!")
                );

        // Message entity banao
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setChatRoom(room);
        message.setType(MessageType.CHAT);

        // Save karo
        Message saved = messageRepository.save(message);

        // DTO mein convert karke return karo
        return convertToDTO(saved);
    }

    // ─── ROOM KI HISTORY FETCH KARO ──────────────────────
    public List<MessageDTO> getMessagesByRoom(Long roomId) {

        // Last 50 messages lo — newest pehle
        List<Message> messages = messageRepository
                .findByChatRoomIdOrderBySentAtDesc(
                        roomId,
                        PageRequest.of(0, 50)
                );
        // Reverse karo — purana pehle dikhega
        Collections.reverse(messages);

        // Reverse karo — purana pehle dikhega chat mein
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    public MessageDTO editMessage(Long messageId, Long userId, String newContent) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message nahi mila!"));

        if (!msg.getSender().getId().equals(userId))
            throw new RuntimeException("Sirf apna message edit kar sakte ho!");

        msg.setContent(newContent);
        msg.setEdited(true);
        Message saved = messageRepository.save(msg);
        return convertToDTO(saved);
    }

    public void deleteMessage(Long messageId, Long userId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message nahi mila!"));

        if (!msg.getSender().getId().equals(userId))
            throw new RuntimeException("Sirf apna message delete kar sakte ho!");

        messageRepository.delete(msg);
    }

    // ─── Entity → DTO convert karo ───────────────────────
    // Frontend ko sirf zaruri data bhejo
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName());
        dto.setSenderPicture(message.getSender().getProfilePicture());
        dto.setRoomId(message.getChatRoom().getId());
        dto.setSentAt(message.getSentAt());
        dto.setType(message.getType());
        dto.setEdited(message.isEdited());
        return dto;
    }
}
