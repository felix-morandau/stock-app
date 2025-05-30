package com.felix_morandau.stock_app.strategy;

import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.service.PortfolioService;

public interface TransactionHandler {
    /** Which transaction type this handler processes */
    TransactionType getType();

    /** Apply the transaction to update portfolio stats */
    void apply(Transaction tx, PortfolioService portfolioService);
}
