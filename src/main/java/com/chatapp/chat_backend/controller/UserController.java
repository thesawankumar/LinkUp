package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    // ─── APNA PROFILE DEKHO ──────────────────────────────
    // GET /api/users/me
    // Frontend OAuth redirect ke baad yeh call karta hai
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User nahi mila!")
                );

        return ResponseEntity.ok(user);
    }
    // GET /api/users/all ← NAYA ADD KARO
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                userRepository.findAllByOrderByNameAsc()
                        .stream()
                        .filter(u -> !u.getEmail().equals(userDetails.getUsername()))
                        .collect(Collectors.toList())
        );
    }
    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User nahi mila!"));
        return ResponseEntity.ok(user);
    }
}
