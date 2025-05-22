package com.felix_morandau.stock_app.entity.transactional;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private float amount = 0.0f;

    @OneToMany
    @JoinColumn(name = "portfolio_id")
    private List<Transaction> transactionList;

    @Column(name = "profit", nullable = false)
    private float profit = 0.0f;

    @OneToMany
    @JoinColumn(name = "portfolio_id")
    private List<StockInfo> stats;

    public void  setDefaultName(String userName) {
        this.name = userName + "'s portfolio";
    }
}
