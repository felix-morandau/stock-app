package com.felix_morandau.stock_app.entity.social;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private String content;

    @Column(name = "user_one_id", nullable = false, updatable = false)
    private UUID userOneId;

    @Column(name = "user_two_id", nullable = false, updatable = false)
    private UUID userTwoId;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    private void prePersist() { timestamp = LocalDateTime.now(); }
}


