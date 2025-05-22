package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/portfolio")
public class PortfolioController {

    //not yet available since the user may have only one portfolio
    @GetMapping("/all/{userId}")
    public List<Portfolio> getUserPortfolios(
            @PathVariable UUID userId
    ) {
        return null;
    }

    @GetMapping("/{userId}/{portfolioName}")
    public Portfolio getUserPortfolioByName(
            @PathVariable UUID userId,
            @PathVariable String portfolioName
    ) {
        return null;
    }
}
