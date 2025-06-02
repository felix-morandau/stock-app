package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.stats.PortfolioStatsDTO;
import com.felix_morandau.stock_app.dto.stats.StockHoldingDTO;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.service.PortfolioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Portfolio>> getUserPortfolios(
            @PathVariable UUID userId
    ) {
        List<Portfolio> list = portfolioService.getUserPortfolios(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{userId}/{portfolioName}")
    public ResponseEntity<Portfolio> getUserPortfolioByName(
            @PathVariable UUID userId,
            @PathVariable String portfolioName
    ) {
        return portfolioService.getUserPortfolios(userId)
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(portfolioName))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{portfolioId}/stats")
    public ResponseEntity<PortfolioStatsDTO> getPortfolioStats(@PathVariable UUID portfolioId) {
        PortfolioStatsDTO stats = portfolioService.getPortfolioStats(portfolioId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{portfolioId}/{stockName}")
    public ResponseEntity<StockHoldingDTO> getStockStats(
            @PathVariable UUID portfolioId,
            @PathVariable String stockName
    ) {
        try {
            // Fetch the portfolio to ensure it exists
            portfolioService.getPortfolioStats(portfolioId); // This will throw PortfolioNotFoundException if not found

            // Find the StockInfo for the given stockName in the portfolio
            StockHoldingDTO stats = portfolioService.getStockHoldingsByPortfolioAndStock(portfolioId, stockName);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            // Handle invalid stock symbol
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Handle other errors (e.g., PortfolioNotFoundException)
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Portfolio> createPortfolio(
            @PathVariable UUID userId,
            @RequestParam String name
    ) {
        Portfolio created = portfolioService.addPortfolio(userId, name);
        return ResponseEntity
                .created(URI.create("/portfolio/" + created.getId()))
                .body(created);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable UUID portfolioId
    ) {
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }
}
