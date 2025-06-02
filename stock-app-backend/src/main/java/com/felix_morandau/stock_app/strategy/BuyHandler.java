package com.felix_morandau.stock_app.strategy;

import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.StockInfo;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.service.PortfolioService;
import com.felix_morandau.stock_app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Component("BUY")
@AllArgsConstructor
public class BuyHandler implements TransactionHandler {
    private final PortfolioService portfolioService;
    private final UserService userService;

    @Override
    public TransactionType getType() {
        return TransactionType.BUY;
    }

    @Override
    @Transactional
    public void apply(Transaction tx, PortfolioService portfolioService, UUID portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        User user = userService.getUserByPortfolioId(portfolioId);

        // Calculate cost of the buy
        double totalCost = tx.getSharePrice() * tx.getNrOfShares();

        // Check if the user has sufficient balance
        if (user.getBalance() < totalCost) {
            throw new IllegalStateException(
                    "Insufficient user balance: need " + totalCost + ", have " + user.getBalance());
        }

        // Deduct the cost from the user's balance
        user.setBalance(user.getBalance() - totalCost);
        userService.save(user);

        // Update portfolio's total invested
        portfolio.setTotalInvested(portfolio.getTotalInvested() + totalCost);

        // Update StockInfo
        Optional<StockInfo> stockOpt = portfolio.getStats().stream()
                .filter(s -> s.getStockName().equals(tx.getStockName()))
                .findFirst();
        StockInfo stockInfo;
        if (stockOpt.isPresent()) {
            stockInfo = stockOpt.get();
            int newShares = stockInfo.getNrOfShares() + tx.getNrOfShares();
            double newAvgCost = ((stockInfo.getAvgCost() * stockInfo.getNrOfShares()) + totalCost) / newShares;
            stockInfo.setNrOfShares(newShares);
            stockInfo.setAvgCost(newAvgCost);
        } else {
            stockInfo = new StockInfo();
            stockInfo.setStockName(tx.getStockName());
            stockInfo.setNrOfShares(tx.getNrOfShares());
            stockInfo.setAvgCost(tx.getSharePrice());
            portfolio.getStats().add(stockInfo); // Add the new StockInfo to the list
        }

        // Update portfolio
        portfolio.setUnrealizedPnl(calculateUnrealizedPnl(portfolio, tx));
        portfolio.setReturnPercentage(calculateReturnPercentage(portfolio));

        portfolioService.save(portfolio); // Save the portfolio, which should cascade to StockInfo
    }

    private double calculateUnrealizedPnl(Portfolio portfolio, Transaction currentTx) {
        double unrealizedPnl = 0.0;

        for (StockInfo stock : portfolio.getStats()) {
            String stockName = stock.getStockName();
            int nrOfShares = stock.getNrOfShares();
            double avgCost = stock.getAvgCost();

            if (stockName.equals(currentTx.getStockName())) {
                double currentPrice = currentTx.getSharePrice();
                unrealizedPnl += (currentPrice - avgCost) * nrOfShares;
            } else {
                Optional<Transaction> latestTx = portfolio.getTransactionList().stream()
                        .filter(tx -> stockName.equals(tx.getStockName()))
                        .max(Comparator.comparing(Transaction::getTimestamp));
                if (latestTx.isPresent()) {
                    double currentPrice = latestTx.get().getSharePrice();
                    unrealizedPnl += (currentPrice - avgCost) * nrOfShares;
                }
            }
        }

        return unrealizedPnl;
    }

    private double calculateReturnPercentage(Portfolio portfolio) {
        if (portfolio.getTotalInvested() == 0) return 0.0;
        return (portfolio.getUnrealizedPnl() / portfolio.getTotalInvested()) * 100;
    }
}