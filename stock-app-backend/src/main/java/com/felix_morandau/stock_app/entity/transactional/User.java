package com.felix_morandau.stock_app.entity.transactional;

import com.felix_morandau.stock_app.entity.enums.Country;
import com.felix_morandau.stock_app.entity.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "country", nullable = false)
    private Country country;

    @OneToMany(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "portfolio_id")
    private List<Portfolio> portfolios;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UserType type;

    @Column(name = "bio")
    private String bio;

    @OneToOne(
            cascade = CascadeType.ALL,
            optional = true
    )
    @JoinColumn(name="settings_id")
    private UserPreferredSettings userPreferredSettings;

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (o instanceof User u) {
            return this.id.equals(u.id);
        }

        return false;
    }
}
