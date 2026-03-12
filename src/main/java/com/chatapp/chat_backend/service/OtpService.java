package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.dtos.AuthResponse;
import com.chatapp.chat_backend.entity.OtpToken;
import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.enums.AuthProvider;
import com.chatapp.chat_backend.repository.OtpTokenRepository;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpTokenRepository otpTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // SecureRandom — cryptographically secure random number
    // Math.random() use mat karo OTP ke liye — unsafe hai!
    private final SecureRandom secureRandom = new SecureRandom();


    @Value("${app.otp.expiry.minutes}")
    private int otpExpiryMinutes;


    // ─── OTP BHEJO ───────────────────────────────────────
    public void sendOtp(String email) {

        // Purane sab OTPs delete karo pehle
        otpTokenRepository.deleteAllByEmail(email);

        // 6 digit OTP banao
        // 100000 to 999999 range
        String otp = String.valueOf(
                100000 + secureRandom.nextInt(900000)
        );

        // Database mein save karo expiry ke saath
        OtpToken otpToken = new OtpToken();
        otpToken.setEmail(email);
        otpToken.setOtp(otp);
        otpToken.setExpiresAt(
                LocalDateTime.now().plusMinutes(otpExpiryMinutes)
        );
        otpTokenRepository.save(otpToken);

        // Email bhejo background mein
        emailService.sendOtpEmail(email, otp);
    }

    // ─── OTP VERIFY KARO ─────────────────────────────────
    public AuthResponse verifyOtp(String email, String otp) {

        // Database mein latest unused OTP dhundo
        OtpToken otpToken = otpTokenRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP nahi mila! Pehle OTP bhejne ki request karo."
                        )
                );

        // Expire ho gaya?
        if (LocalDateTime.now().isAfter(otpToken.getExpiresAt())) {
            throw new RuntimeException(
                    "OTP expire ho gaya! Naaya OTP mangwao."
            );
        }

        // OTP match karta hai?
        if (!otpToken.getOtp().equals(otp)) {
            throw new RuntimeException(
                    "Galat OTP! Dobara try karo."
            );
        }

        // OTP sahi hai — used mark karo taaki reuse na ho
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        // User hai toh login, nahi hai toh auto-register!
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {

                    // Email se naam banao
                    // "sawan.kumar@gmail.com" → "Sawan Kumar"
                    String rawName = email.split("@")[0]
                            .replace(".", " ")
                            .replace("_", " ");

                    StringBuilder name = new StringBuilder();
                    for (String word : rawName.split(" ")) {
                        if (!word.isEmpty()) {
                            name.append(
                                            Character.toUpperCase(word.charAt(0))
                                    )
                                    .append(word.substring(1).toLowerCase())
                                    .append(" ");
                        }
                    }

                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name.toString().trim());
                    newUser.setProvider(AuthProvider.OTP);
                    return userRepository.save(newUser);
                });

        // JWT token banao aur bhejo
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfilePicture()
        );
    }
}
