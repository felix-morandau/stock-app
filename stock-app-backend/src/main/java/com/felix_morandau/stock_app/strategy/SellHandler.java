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

@Component("SELL")
@AllArgsConstructor
public class SellHandler implements TransactionHandler {

    private final PortfolioService portfolioService;
    private final UserService userService; // Inject UserService

    @Override
    public TransactionType getType() {
        return TransactionType.SELL;
    }

    @Override
    @Transactional
    public void apply(Transaction tx, PortfolioService portfolioService, UUID portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        User user = userService.getUserByPortfolioId(portfolioId);

        Optional<StockInfo> stockOpt = portfolio.getStats().stream()
                .filter(s -> s.getStockName().equals(tx.getStockName()))
                .findFirst();
        if (stockOpt.isPresent()) {
            StockInfo stockInfo = stockOpt.get();
            int sharesToSell = tx.getNrOfShares();
            double totalSale = tx.getSharePrice() * sharesToSell;

            // Add the sale proceeds to the user's balance
            user.setBalance(user.getBalance() + totalSale);
            userService.save(user); // Save the updated user balance

            stockInfo.setNrOfShares(stockInfo.getNrOfShares() - sharesToSell);
            if (stockInfo.getNrOfShares() == 0) {
                portfolio.getStats().remove(stockInfo);
            }

            // Update realized P&L
            double profit = totalSale - (stockInfo.getAvgCost() * sharesToSell);
            portfolio.setProfit(portfolio.getProfit() + profit);

            // Update portfolio
            portfolio.setUnrealizedPnl(calculateUnrealizedPnl(portfolio, tx));
            portfolio.setReturnPercentage(calculateReturnPercentage(portfolio));

            portfolioService.save(portfolio);
        } else {
            throw new IllegalStateException("Stock not found in portfolio: " + tx.getStockName());
        }
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
        return ((portfolio.getUnrealizedPnl()) / portfolio.getTotalInvested()) * 100;
    }
}