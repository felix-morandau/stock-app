package com.felix_morandau.stock_app.entity.transactional;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "total_invested", nullable = false)
    private double totalInvested;

    @OneToMany
    @JoinColumn(name = "portfolio_id")
    private List<Transaction> transactionList;

    @Column(name = "realized_pnl", nullable = false)
    private double realizedPnl;

    @Column(name = "unrealized_pnl", nullable = false)
    private double unrealizedPnl;

    @Column(name = "return_percentage", nullable = false)
    private double returnPercentage;

    @OneToMany
    @JoinColumn(name = "portfolio_id")
    private List<StockInfo> stats;

    public void  setDefaultName(String userName) {
        this.name = userName + "'s portfolio";
    }
}
