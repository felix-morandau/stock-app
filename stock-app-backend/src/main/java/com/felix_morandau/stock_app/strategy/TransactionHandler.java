package com.felix_morandau.stock_app.strategy;

import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.service.PortfolioService;

import java.util.UUID;

public interface TransactionHandler {
    TransactionType getType();
    void apply(Transaction tx, PortfolioService portfolioService, UUID portfolioId);
}