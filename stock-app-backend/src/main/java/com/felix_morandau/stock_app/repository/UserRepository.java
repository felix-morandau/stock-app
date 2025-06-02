package com.felix_morandau.stock_app.repository;

import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.entity.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserById(UUID userId);

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.portfolios p WHERE p.id = :portfolioId")
    Optional<User> findUserByPortfolioId(@Param("portfolioId") UUID portfolioId);

    List<User> findUsersByType(UserType type);
}
