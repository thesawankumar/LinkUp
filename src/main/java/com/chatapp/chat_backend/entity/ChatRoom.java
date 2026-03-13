package com.chatapp.chat_backend.entity;

import com.chatapp.chat_backend.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
@Data
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

//    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "room_members",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    // Members field pe:
    @ManyToMany
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "members"})
    private Set<User> members = new HashSet<>();
    // Room ka type — GROUP ya DIRECT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'GROUP'")
    private RoomType roomType = RoomType.GROUP;

    @ManyToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "members", "password"})
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();
}
