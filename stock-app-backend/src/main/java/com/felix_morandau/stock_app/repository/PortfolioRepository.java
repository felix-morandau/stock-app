package com.felix_morandau.stock_app.repository;

import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

    Optional<Portfolio> findPortfolioById(UUID portfolioId);

    @Query("""
      SELECT p
      FROM User u
      JOIN u.portfolios p
      WHERE u.id = :userId
    """)
    List<Portfolio> findPortfoliosByUser(@Param("userId") UUID userId);
}
