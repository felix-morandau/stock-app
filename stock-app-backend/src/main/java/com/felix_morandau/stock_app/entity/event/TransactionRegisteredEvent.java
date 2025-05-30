package com.felix_morandau.stock_app.entity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class TransactionRegisteredEvent extends ApplicationEvent {
    private final UUID transactionId;

    public TransactionRegisteredEvent(Object source, UUID transactionId) {
        super(source);
        this.transactionId = transactionId;
    }

}
