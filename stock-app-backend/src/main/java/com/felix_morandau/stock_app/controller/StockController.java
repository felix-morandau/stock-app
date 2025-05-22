package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.stocks.StockDataDTO;
import com.felix_morandau.stock_app.entity.enums.StockSymbol;
import com.felix_morandau.stock_app.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@CrossOrigin
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{symbol}/monthly")
    public ResponseEntity<StockDataDTO> getMonthly(@PathVariable String symbol) {
        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(symbol.toUpperCase());
            StockDataDTO response = stockService.fetchMonthHistory(stockSymbol);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{symbol}/daily")
    public ResponseEntity<StockDataDTO> getDaily(@PathVariable String symbol,
                                                 @RequestParam(required = false) String date) {
        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(symbol.toUpperCase());
            LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
            StockDataDTO response = stockService.fetchDayData(stockSymbol, targetDate);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{symbol}/current")
    public ResponseEntity<Double> getCurrent(@PathVariable String symbol) {
        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(symbol.toUpperCase());
            Double price = stockService.getCurrentPrice(stockSymbol);

            return (price != null) ? ResponseEntity.ok(price) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
