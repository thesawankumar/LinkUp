package com.chatapp.chat_backend.entity;

import com.chatapp.chat_backend.enums.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom chatRoom;

    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean edited = false;

    // Existing fields ke saath add karo
    @Column(nullable = true)
    private String fileUrl;      // file ka path

    @Column(nullable = true)
    private String fileType;     // "image", "video", "file"

    @Column(nullable = true)
    private String fileName;     // original file name

    @Enumerated(EnumType.STRING)
    private MessageType type = MessageType.CHAT;
}