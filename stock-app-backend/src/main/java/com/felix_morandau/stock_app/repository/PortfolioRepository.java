package com.felix_morandau.stock_app.repository;

import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

    Optional<Portfolio> findPortfolioById(UUID portfolioId);
}
