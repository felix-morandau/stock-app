package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.dto.transactions.CreateDepositWithdrawDTO;
import com.felix_morandau.stock_app.dto.transactions.CreateTransactionDTO;
import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.event.TransactionRegisteredEvent;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.repository.PortfolioRepository;
import com.felix_morandau.stock_app.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {
    private final ApplicationEventPublisher publisher;
    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;

    public List<Transaction> getTransactions(String stockName, UUID portfolioId) {
        return transactionRepository.findByPortfolioAndStock(portfolioId, stockName);
    }

    /**
     * Save a buy/sell/dividend/split transaction, then publish an event.
     */
    @Transactional
    public Transaction addTransaction(CreateTransactionDTO dto,
                                      TransactionType type,
                                      UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Portfolio with id " + portfolioId + " was not found"));

        Transaction tx = new Transaction();
        tx.setTransactionType(type);
        tx.setTimestamp(LocalDateTime.now());
        tx.setNotes(dto.getNotes());
        tx.setNrOfShares(dto.getNrOfShares());
        tx.setSharePrice(dto.getSharePrice());
        tx.setStockName(dto.getStockName());

        // Persist the transaction
        Transaction saved = transactionRepository.save(tx);

        portfolio.getTransactionList().add(saved);
        portfolioRepository.save(portfolio);

        publisher.publishEvent(new TransactionRegisteredEvent(this, saved.getId()));

        return saved;
    }

    /**
     * Save a deposit/withdrawal (external cash) transaction, then publish an event.
     */
    @Transactional
    public Transaction addExternalOperation(CreateDepositWithdrawDTO dto,
                                            TransactionType type,
                                            UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Portfolio with id " + portfolioId + " was not found"));

        Transaction tx = new Transaction();
        tx.setTransactionType(type);
        tx.setTimestamp(LocalDateTime.now());
        tx.setNotes(dto.getNotes());
        tx.setCashAmount(dto.getCashAmount());

        Transaction saved = transactionRepository.save(tx);

        portfolio.getTransactionList().add(saved);
        portfolioRepository.save(portfolio);

        publisher.publishEvent(new TransactionRegisteredEvent(this, saved.getId()));

        return saved;
    }
}
