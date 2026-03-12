package com.chatapp.chat_backend.dtos;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private String profilePicture;

    // Constructor — jab bhi JWT banayenge tab yeh use hoga
    public AuthResponse(String token, Long userId, String name,
                        String email, String profilePicture) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }
}