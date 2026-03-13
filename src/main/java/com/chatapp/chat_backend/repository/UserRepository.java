package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    // Spring yeh method dekh ke khud SQL banata hai:
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
    // Saare users lo — DM ke liye
    List<User> findAllByOrderByNameAsc();
}
