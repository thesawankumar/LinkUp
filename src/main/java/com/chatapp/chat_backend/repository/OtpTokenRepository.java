package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    // Sabse latest unused OTP dhundo is email ke liye
    Optional<OtpToken> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    // Purane sab OTPs delete karo before sending new one
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpToken o WHERE o.email = :email")
    void deleteAllByEmail(String email);
}
