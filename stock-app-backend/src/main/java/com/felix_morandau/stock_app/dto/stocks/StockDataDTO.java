package com.felix_morandau.stock_app.dto.stocks;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class StockDataDTO {
    private String symbol;

    private Map<LocalDateTime, StockCandleDTO> hourlyPrices;
}
