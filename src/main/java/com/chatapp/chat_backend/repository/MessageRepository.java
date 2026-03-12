package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomIdOrderBySentAtDesc(
            Long chatRoomId, Pageable pageable
    );
}
