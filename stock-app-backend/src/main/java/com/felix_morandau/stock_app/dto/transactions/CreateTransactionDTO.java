package com.felix_morandau.stock_app.dto.transactions;

import com.felix_morandau.stock_app.entity.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTransactionDTO {
    @NotNull
    private TransactionType transactionType;

    @NotNull
    private String notes;

    @NotNull
    private String stockName;

    @NotNull
    private float sharePrice;

    @NotNull
    private int nrOfShares;
}
