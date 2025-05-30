package com.felix_morandau.stock_app.listener;

import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.event.TransactionRegisteredEvent;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.repository.TransactionRepository;
import com.felix_morandau.stock_app.service.PortfolioService;
import com.felix_morandau.stock_app.strategy.TransactionHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TransactionRegisteredListener {

    private final TransactionRepository txRepo;
    private final PortfolioService portfolioService;
    private final Map<TransactionType, TransactionHandler> handlers;

    public TransactionRegisteredListener(
            TransactionRepository txRepo,
            PortfolioService portfolioService,
            List<TransactionHandler> handlerBeans
    ) {
        this.txRepo = txRepo;
        this.portfolioService = portfolioService;
        this.handlers = handlerBeans.stream()
                .collect(Collectors.toMap(
                        TransactionHandler::getType,
                        handler -> handler
                ));
    }

    /**
     * Listens for TransactionRegisteredEvent *after* the DB commit,
     * and processes it asynchronously on the configured executor.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransactionRegistered(TransactionRegisteredEvent event) {
        UUID txId = event.getTransactionId();

        Transaction tx = txRepo.findById(txId)
                .orElseThrow(() ->
                        new IllegalStateException("Transaction not found: " + txId)
                );

        TransactionType type = tx.getTransactionType();
        TransactionHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalStateException(
                    "No handler registered for transaction type: " + type
            );
        }

        handler.apply(tx, portfolioService);
    }
}
