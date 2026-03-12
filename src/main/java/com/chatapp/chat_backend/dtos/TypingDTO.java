package com.chatapp.chat_backend.dtos;

import lombok.Data;

@Data
public class TypingDTO {

    private String email;
    private String name;
    private Long roomId;
    private boolean typing;
}
