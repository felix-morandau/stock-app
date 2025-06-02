package com.felix_morandau.stock_app.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioStatsDTO {
    private double totalValue; // Current market value of all stocks
    private double totalInvested; // Total amount invested
    private double realizedPnl; // Realized profit/loss
    private double unrealizedPnl; // Unrealized profit/loss
    private double returnPercentage; // Return percentage
    private List<StockHoldingDTO> holdings; // Breakdown of stock holdings
}
