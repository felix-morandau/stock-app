package com.felix_morandau.stock_app.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHoldingDTO {
    private String stockName;
    private int numberOfShares;
    private double averageCost;
    private double currentPrice;
    private double currentValue; // numberOfShares * currentPrice
    private double unrealizedPnl;
}
