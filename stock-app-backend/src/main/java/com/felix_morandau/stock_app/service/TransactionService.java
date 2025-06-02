package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.dto.transactions.CreateDepositWithdrawDTO;
import com.felix_morandau.stock_app.dto.transactions.CreateTransactionDTO;
import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.event.TransactionRegisteredEvent;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.StockInfo;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {
    private final ApplicationEventPublisher publisher;
    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private final UserService userService;

    public java.util.List<Transaction> getTransactions(String stockName, UUID portfolioId) {
        return transactionRepository.findByPortfolioAndStock(portfolioId, stockName);
    }

    /**
     * Save a buy/sell/dividend/split transaction, then publish an event.
     * Preconditions (like “enough cash” or “enough shares”) are checked BEFORE saving.
     */
    @Transactional
    public Transaction addTransaction(CreateTransactionDTO dto,
                                      TransactionType type,
                                      UUID portfolioId) {
        // 1) Load the portfolio
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);

        // 2) Check preconditions (throws IllegalStateException if invalid)
        checkPreconditions(dto.getStockName(),
                dto.getNrOfShares(),
                dto.getSharePrice(),
                type,
                portfolio);

        // 3) Build & save the Transaction entity
        Transaction tx = new Transaction();
        tx.setTransactionType(type);
        tx.setTimestamp(LocalDateTime.now());
        tx.setNotes(dto.getNotes());
        tx.setNrOfShares(dto.getNrOfShares());
        tx.setSharePrice(dto.getSharePrice());
        tx.setStockName(dto.getStockName());
        Transaction saved = transactionRepository.save(tx);

        // 4) Link transaction → portfolio (so JPA writes FK) and save the portfolio
        portfolio.getTransactionList().add(saved);
        portfolioService.save(portfolio);

        // 5) Publish the event AFTER commit
        publisher.publishEvent(new TransactionRegisteredEvent(this, saved.getId(), portfolioId));
        return saved;
    }

    /**
     * Save a deposit/withdrawal (external cash) transaction, then publish an event.
     * Preconditions ensure that on a WITHDRAW, there is enough cash to withdraw.
     */
    @Transactional
    public Transaction addExternalOperation(CreateDepositWithdrawDTO dto,
                                            TransactionType type,
                                            UUID portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);

        // Check if WITHDRAW is possible; for DEPOSIT there’s no restriction
        if (type == TransactionType.WITHDRAW) {
            if (portfolio.getProfit() < dto.getCashAmount()) {
                throw new IllegalStateException(
                        "Insufficient cash for withdrawal: requested " + dto.getCashAmount() +
                                ", available " + portfolio.getProfit());
            }
        }

        Transaction tx = new Transaction();
        tx.setTransactionType(type);
        tx.setTimestamp(LocalDateTime.now());
        tx.setNotes(dto.getNotes());
        tx.setCashAmount(dto.getCashAmount());
        Transaction saved = transactionRepository.save(tx);

        portfolio.getTransactionList().add(saved);
        portfolioService.save(portfolio);

        publisher.publishEvent(new TransactionRegisteredEvent(this, saved.getId(), portfolioId));
        return saved;
    }

    /**
     * Verify that a BUY or SELL is actually possible.
     * Throws IllegalStateException if any rule is violated.
     */
    private void checkPreconditions(String stockName,
                                    Integer nrOfShares,
                                    double sharePrice,
                                    TransactionType type,
                                    Portfolio portfolio) {

        User user = userService.getUserByPortfolioId(portfolio.getId());

        switch (type) {
            case BUY -> {
                double cost = sharePrice * nrOfShares;
                if (user.getBalance() < cost) {
                    throw new IllegalStateException(
                            "Insufficient funds: need " + cost + ", have " + user.getBalance());
                }

                user.setBalance(user.getBalance() - cost);
                userService.save(user);
            }

            case SELL -> {
                Optional<StockInfo> maybeInfo = portfolio.getStats().stream()
                        .filter(si -> si.getStockName().equals(stockName))
                        .findFirst();

                if (maybeInfo.isEmpty()) {
                    throw new IllegalStateException(
                            "Cannot sell: no position in " + stockName);
                }
                StockInfo info = maybeInfo.get();
                if (info.getNrOfShares() < nrOfShares) {
                    throw new IllegalStateException(
                            "Cannot sell " + nrOfShares + " shares of " + stockName +
                                    " – only " + info.getNrOfShares() + " owned");
                }

                user.setBalance(user.getBalance() + sharePrice * nrOfShares);
            }

            default -> throw new IllegalArgumentException(
                    "Unhandled transaction type: " + type);
        }
    }
}
