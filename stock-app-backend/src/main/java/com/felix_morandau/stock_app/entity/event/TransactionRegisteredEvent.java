package com.felix_morandau.stock_app.entity.event;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransactionRegisteredEvent {
    private final Object source;
    private final UUID transactionId;
    private final UUID portfolioId;

    public TransactionRegisteredEvent(Object source, UUID transactionId, UUID portfolioId) {
        this.source = source;
        this.transactionId = transactionId;
        this.portfolioId = portfolioId;
    }
}