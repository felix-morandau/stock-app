package com.felix_morandau.stock_app.entity.transactional;

import com.felix_morandau.stock_app.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "notes")
    private String notes;

    //buy/sell operations
    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "share_price")
    private Float sharePrice;

    @Column(name = "nr_of_shares")
    private Integer nrOfShares;

    //deposit/withdraw operations
    @Column(name = "cash_amount")
    private Float cashAmount;

}

