package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.entity.ChatRoom;
import com.chatapp.chat_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // Do users ke beech direct room dhundo
    @Query("SELECT r FROM ChatRoom r WHERE r.roomType = 'DIRECT' AND " +
            ":user1 MEMBER OF r.members AND :user2 MEMBER OF r.members")
    Optional<ChatRoom> findDirectRoom(
            @Param("user1") User user1,
            @Param("user2") User user2
    );
}
