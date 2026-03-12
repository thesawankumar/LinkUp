package com.chatapp.chat_backend.entity;

import com.chatapp.chat_backend.enums.AuthProvider;
import com.chatapp.chat_backend.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String password; // Null for Google/OTP users

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    private boolean online = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastSeen;
}