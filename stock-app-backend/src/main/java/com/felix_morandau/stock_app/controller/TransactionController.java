package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.transactions.CreateDepositWithdrawDTO;
import com.felix_morandau.stock_app.dto.transactions.CreateTransactionDTO;
import com.felix_morandau.stock_app.entity.enums.TransactionType;
import com.felix_morandau.stock_app.entity.transactional.Transaction;
import com.felix_morandau.stock_app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam(required = false) UUID portfolioId,
            @RequestParam(required = false) String stockName) {

        List<Transaction> list = transactionService.getTransactions(stockName, portfolioId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/sell/{portfolioId}")
    public ResponseEntity<Transaction> addSell(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody CreateTransactionDTO dto) {

        Transaction t = transactionService.addTransaction(dto, TransactionType.SELL, portfolioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(t);
    }

    @PostMapping("/buy/{portfolioId}")
    public ResponseEntity<Transaction> addPurchase(
            @PathVariable UUID portfolioId,
            @Valid@RequestBody CreateTransactionDTO dto) {

        Transaction t = transactionService.addTransaction(dto, TransactionType.BUY, portfolioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(t);
    }

    @PostMapping("/deposit/{portfolioId}")
    public ResponseEntity<Transaction> addDeposit(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody CreateDepositWithdrawDTO dto) {

        Transaction t = transactionService.addExternalOperation(dto, TransactionType.DEPOSIT, portfolioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(t);
    }

    @PostMapping("/withdraw/{portfolioId}")
    public ResponseEntity<Transaction> addWithdrawal(
            @PathVariable UUID portfolioId,
            @Valid @RequestBody CreateDepositWithdrawDTO dto) {

        Transaction t = transactionService.addExternalOperation(dto, TransactionType.WITHDRAW, portfolioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(t);
    }
}
