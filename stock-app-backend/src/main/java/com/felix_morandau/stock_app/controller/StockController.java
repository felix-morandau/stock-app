package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.stocks.StockDataDTO;
import com.felix_morandau.stock_app.entity.enums.StockSymbol;
import com.felix_morandau.stock_app.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{symbol}/monthly/{userId}")
    public ResponseEntity<StockDataDTO> getMonthly(
            @PathVariable String symbol,
            @PathVariable UUID userId
    ) {
        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(symbol.toUpperCase());
            StockDataDTO response = stockService.fetchMonthHistory(stockSymbol, userId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{symbol}/daily/{userId}")
    public ResponseEntity<StockDataDTO> getDaily(
            @PathVariable String symbol,
            @PathVariable UUID userId,
            @RequestParam(required = false) String date
    ){
        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(symbol.toUpperCase());
            LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
            StockDataDTO response = stockService.fetchDayData(stockSymbol, targetDate, userId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{symbol}/current/{userId}")
    public ResponseEntity<Double> getCurrent(
            @PathVariable String symbol,
            @PathVariable UUID userId
    ) {
        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(symbol.toUpperCase());
            Double price = stockService.getCurrentPrice(stockSymbol, userId);

            return (price != null) ? ResponseEntity.ok(price) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
