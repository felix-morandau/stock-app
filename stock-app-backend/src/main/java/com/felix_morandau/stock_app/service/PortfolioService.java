package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.config.PortfolioNotFoundException;
import com.felix_morandau.stock_app.dto.stats.PortfolioStatsDTO;
import com.felix_morandau.stock_app.dto.stats.StockHoldingDTO;
import com.felix_morandau.stock_app.entity.enums.StockSymbol;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.StockInfo;
import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;
    private final UserService userService;

    protected Portfolio addDefaultPortfolio(UUID userId) {
        User user = userService.getUser(userId);

        Portfolio portfolio = new Portfolio();
        portfolio.setDefaultName(user.getFirstName());
        portfolio.setProfit(0.0f);

        return portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getUserPortfolios(UUID userId) {
        return portfolioRepository.findPortfoliosByUser(userId);
    }

    public Portfolio addPortfolio(UUID userId, String name) {
        User user = userService.getUser(userId);

        Portfolio portfolio = new Portfolio();
        portfolio.setName(name);
        user.getPortfolios().add(portfolio);

        return portfolioRepository.save(portfolio);
    }

    public void deletePortfolio(UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        portfolioRepository.delete(portfolio);
    }

    public void save(Portfolio portfolio) {
        portfolioRepository.save(portfolio);
    }

    public Portfolio getPortfolioById(UUID portfolioId) {
        return portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));
    }

    public PortfolioStatsDTO getPortfolioStats(UUID portfolioId) {
        // Fetch the portfolio
        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        User user = userService.getUserByPortfolioId(portfolioId);
        // Initialize the DTO
        PortfolioStatsDTO statsDTO = new PortfolioStatsDTO();

        // Calculate stock holdings and related metrics
        List<StockHoldingDTO> holdings = new ArrayList<>();
        double totalValue = 0.0;
        double totalInvested = 0.0;
        double totalUnrealizedPnl = 0.0;

        for (StockInfo stockInfo : portfolio.getStats()) {
            StockHoldingDTO holding = getStockHoldings(stockInfo, user.getId());
            holdings.add(holding);
            totalValue += holding.getCurrentValue();
            totalInvested += holding.getAverageCost() * holding.getNumberOfShares();
            totalUnrealizedPnl += holding.getUnrealizedPnl();
        }

        // Set the calculated metrics
        double totalValueWithBalance = totalValue + user.getBalance();
        statsDTO.setTotalValue(totalValueWithBalance);
        statsDTO.setTotalInvested(totalInvested);
        statsDTO.setUnrealizedPnl(totalUnrealizedPnl);
        statsDTO.setHoldings(holdings);

        // Calculate return percentage
        if (totalInvested > 0) {
            statsDTO.setReturnPercentage(((totalValueWithBalance - totalInvested) / totalInvested) * 100);
        } else {
            statsDTO.setReturnPercentage(0.0);
        }

        return statsDTO;
    }

    private StockHoldingDTO getStockHoldings(StockInfo stockInfo, UUID userId) {
        StockHoldingDTO holding = new StockHoldingDTO();
        holding.setStockName(stockInfo.getStockName());
        holding.setNumberOfShares(stockInfo.getNrOfShares());
        holding.setAverageCost(stockInfo.getAvgCost());

        try {
            StockSymbol stockSymbol = StockSymbol.valueOf(stockInfo.getStockName());
            double currentPrice = stockService.getCurrentPrice(stockSymbol, userId);
            holding.setCurrentPrice(currentPrice);

            double currentValue = stockInfo.getNrOfShares() * currentPrice;
            double unrealizedPnl = (currentPrice - stockInfo.getAvgCost()) * stockInfo.getNrOfShares();

            holding.setCurrentValue(currentValue);
            holding.setUnrealizedPnl(unrealizedPnl);
        } catch (IllegalArgumentException e) {
            // Handle invalid stock symbol
            holding.setCurrentPrice(0.0);
            holding.setCurrentValue(0.0);
            holding.setUnrealizedPnl(0.0);
            System.err.println("Invalid stock symbol: " + stockInfo.getStockName());
        } catch (Exception e) {
            // Handle stock price fetch failure
            holding.setCurrentPrice(0.0);
            holding.setCurrentValue(0.0);
            holding.setUnrealizedPnl(0.0);
            System.err.println("Failed to fetch price for stock: " + stockInfo.getStockName());
        }

        return holding;
    }

    public StockHoldingDTO getStockHoldingsByPortfolioAndStock(UUID portfolioId, String stockName) {
        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException(portfolioId));

        User user = userService.getUserByPortfolioId(portfolioId);

        StockInfo stockInfo = portfolio.getStats().stream()
                .filter(si -> si.getStockName().equalsIgnoreCase(stockName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Stock not found in portfolio: " + stockName));

        return getStockHoldings(stockInfo, user.getId());
    }
}