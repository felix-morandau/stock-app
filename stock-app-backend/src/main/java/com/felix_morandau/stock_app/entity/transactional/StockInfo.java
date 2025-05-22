package com.felix_morandau.stock_app.entity.transactional;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "stats")
@Data
public class StockInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "stock_name", unique = true, nullable = false)
    private String stockName;

    @Column(name = "nr_of_shares", nullable = false)
    private int nrOfShares;

    @Column(name = "avg_cost", nullable = false)
    private float avgCost;

    @Column(name = "unrealized_gain", nullable = false)
    private float unrealizedGain;

    @Column(name = "realized_gain", nullable = false)
    private float realizedGain;
}
