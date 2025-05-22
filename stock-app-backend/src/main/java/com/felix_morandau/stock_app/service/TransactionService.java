package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.dto.transactions.CreateDepositWithdrawDTO;
import com.felix_morandau.stock_app.dto.transactions.CreateTransactionDTO;
import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.transactional.Portfolio;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.repository.PortfolioRepository;
import com.felix_morandau.stock_app.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;

    public List<Transaction> getTransactions(String stockName, UUID portfolioId) {

        return transactionRepository.findByPortfolioAndStock(portfolioId, stockName);
    }

    public Transaction addTransaction(CreateTransactionDTO dto, TransactionType type, UUID portfolioId) {
        Transaction transaction = new Transaction();

        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                        .orElseThrow(() -> new EntityNotFoundException("Portfolio with id " + portfolioId + "was not found"));

        transaction.setTransactionType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setNotes(dto.getNotes());
        transaction.setNrOfShares(dto.getNrOfShares());
        transaction.setSharePrice(dto.getSharePrice());
        transaction.setStockName(dto.getStockName());

        portfolio.getTransactionList().add(transaction);
        portfolioRepository.save(portfolio);

        return transactionRepository.save(transaction);
    }

    public Transaction addExternalOperation(CreateDepositWithdrawDTO dto, TransactionType type, UUID portfolioId) {
        Transaction transaction = new Transaction();

        Portfolio portfolio = portfolioRepository.findPortfolioById(portfolioId)
                        .orElseThrow(() -> new EntityNotFoundException("Portfolio with id " + portfolioId + "was not found"));

        transaction.setTransactionType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setNotes(dto.getNotes());
        transaction.setCashAmount(dto.getCashAmount());

        portfolio.getTransactionList().add(transaction);
        portfolioRepository.save(portfolio);

        return transactionRepository.save(transaction);
    }
}
