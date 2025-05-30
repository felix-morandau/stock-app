package com.felix_morandau.stock_app.controller;

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
