package com.chatapp.chat_backend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Secret key banao signing ke liye
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ─── TOKEN BANAO ──────────────────────────────────────
    // Email deke token banata hai — login ke baad call hota hai
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                          // Token mein email store karo
                .issuedAt(new Date())                    // Kab banaya
                .expiration(new Date(
                        System.currentTimeMillis() + expiration  // Kab expire hoga
                ))
                .signWith(getSigningKey())               // Secret se sign karo
                .compact();                              // String mein convert karo
    }

    // ─── EMAIL NIKALO ─────────────────────────────────────
    // Token deke email wapas nikalta hai
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();                           // Email yahan store thi
    }

    // ─── TOKEN VALID HAI? ─────────────────────────────────
    // Signature match karta hai? Expire toh nahi hua?
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;                             // Sab theek hai
        } catch (JwtException | IllegalArgumentException e) {
            return false;                            // Token kharab hai
        }
    }
}
