package com.felix_morandau.stock_app.repository;

import com.felix_morandau.stock_app.entity.transactional.UserPreferredSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SettingsRepository extends JpaRepository<UserPreferredSettings, UUID> {

    @Query("""
      SELECT s
        FROM User u
        JOIN u.userPreferredSettings s
       WHERE u.id = :userId
    """)

    Optional<UserPreferredSettings> getByUserId(@Param("userId") UUID userId);
}

