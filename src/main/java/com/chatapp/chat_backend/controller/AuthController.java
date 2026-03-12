package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.dtos.*;
import com.chatapp.chat_backend.service.AuthService;
import com.chatapp.chat_backend.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;


    // ─── REGISTER ────────────────────────────────────────
    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    // ─── LOGIN ───────────────────────────────────────────
    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // ─── OTP BHEJO ───────────────────────────────────────
    // POST /api/auth/otp/send
    @PostMapping("/otp/send")
    public ResponseEntity<Map<String, String>> sendOtp(
            @Valid @RequestBody OtpRequest req) {
        otpService.sendOtp(req.getEmail());
        return ResponseEntity.ok(
                Map.of("message", "OTP bhej diya! 10 minutes mein expire hoga.")
        );
    }

    // ─── OTP VERIFY ──────────────────────────────────────
    // POST /api/auth/otp/verify
    @PostMapping("/otp/verify")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest req) {
        return ResponseEntity.ok(
                otpService.verifyOtp(req.getEmail(), req.getOtp())
        );
    }

}
