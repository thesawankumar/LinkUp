package com.chatapp.chat_backend.dtos;


import com.chatapp.chat_backend.enums.MessageType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private Long id;
    private String content;
    private Long senderId;
    private String senderName;
    private String senderPicture;
    private Long roomId;
    private LocalDateTime sentAt;
    private MessageType type;
    private boolean edited;
}