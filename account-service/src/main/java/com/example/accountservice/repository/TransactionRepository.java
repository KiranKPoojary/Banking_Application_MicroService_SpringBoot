package com.example.accountservice.repository;

import com.example.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromaccount_IdOrToaccount_IdOrderByTransactionDateDesc(Long fromAccountId, Long toAccountId);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    Optional<Transaction> findByTransactionId(String transactionId);
}
