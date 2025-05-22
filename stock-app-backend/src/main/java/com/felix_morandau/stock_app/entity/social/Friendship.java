package com.felix_morandau.stock_app.entity.social;

import com.felix_morandau.stock_app.entity.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(
        name = "friendships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_one_id", "user_two_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @EmbeddedId
    private FriendshipId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Messages that belong to this friendship.
     * The FK columns are the same two that make up the PK, so we reference them
     * with a pair of @JoinColumn annotations.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({
            @JoinColumn(name = "user_one_id", referencedColumnName = "user_one_id",
                    insertable = false, updatable = false),
            @JoinColumn(name = "user_two_id", referencedColumnName = "user_two_id",
                    insertable = false, updatable = false)
    })
    private List<Message> messages = new LinkedList<>();

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
