package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.dtos.AuthResponse;
import com.chatapp.chat_backend.dtos.LoginRequest;
import com.chatapp.chat_backend.dtos.RegisterRequest;
import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.enums.AuthProvider;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest req)
    {
        // Email pehle se registered hai?
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        // Naya user object banao
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());

        // Password HAMESHA hash karke save karo — kabhi plain text nahi!
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setProvider(AuthProvider.LOCAL);

        // Database mein save karo
        User savedUser = userRepository.save(user);

        // JWT token banao
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getProfilePicture()
        );

    }


    // ─── LOGIN ───────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {

        // Spring Security email + password verify karega
        // Galat password → BadCredentialsException automatic aayega
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        // Verified! Ab user lo database se
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        // Token banao
        String token = jwtUtil.generateToken(email);

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfilePicture()
        );
    }
}
