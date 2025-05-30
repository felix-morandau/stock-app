package com.felix_morandau.stock_app.repository;

import com.felix_morandau.stock_app.entity.transactional.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
SELECT t
FROM Portfolio p
JOIN p.transactionList t
WHERE p.id = :portfolioId
""")
    List<Transaction> findAllByPortfolioId(@Param("portfolioId") UUID portfolioId);

    @Query("""
  SELECT t
  FROM Portfolio p
  JOIN p.transactionList t
  WHERE (:portfolioId IS NULL OR p.id = :portfolioId)
    AND (:stockName   IS NULL OR t.stockName = :stockName)
 """)
    List<Transaction> findByPortfolioAndStock(
            @Param("portfolioId") UUID portfolioId,
            @Param("stockName")   String stockName
    );


}
