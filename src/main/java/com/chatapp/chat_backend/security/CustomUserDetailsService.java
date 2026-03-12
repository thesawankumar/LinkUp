package com.chatapp.chat_backend.security;

import com.chatapp.chat_backend.entity.User;
import com.chatapp.chat_backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    // Spring Security yeh method call karta hai jab bhi
    // user ko load karna ho
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Database mein email se user dhundo
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        // Spring Security ka UserDetails return karo
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),

                // Google/OTP users ka password null hota hai
                // Empty string de do — JWT use karte hain hum waise bhi
                user.getPassword() != null ? user.getPassword() : "",

                // User ka role — ROLE_USER ya ROLE_ADMIN
                Collections.singletonList(
                        new SimpleGrantedAuthority(user.getRole().name())
                )
        );
    }
}
