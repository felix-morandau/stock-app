package com.felix_morandau.stock_app.entity.transactional;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "stats")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
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
}
