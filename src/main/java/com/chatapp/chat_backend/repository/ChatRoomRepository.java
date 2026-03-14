package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.entity.ChatRoom;
import com.chatapp.chat_backend.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // ← Yahi fix hai — User object nahi, Long id se query
    @Query("SELECT r FROM ChatRoom r JOIN r.members m1 JOIN r.members m2 " +
            "WHERE r.roomType = :roomType " +
            "AND m1.id = :userId1 AND m2.id = :userId2 " +
            "AND SIZE(r.members) = 2")
    Optional<ChatRoom> findDirectRoom(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("roomType") RoomType roomType
    );
}