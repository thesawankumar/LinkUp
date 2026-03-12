package com.chatapp.chat_backend.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    private String email;
}
