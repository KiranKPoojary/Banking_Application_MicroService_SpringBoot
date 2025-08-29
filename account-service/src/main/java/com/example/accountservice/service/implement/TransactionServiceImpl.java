package com.example.accountservice.service.implement;

import com.example.accountservice.dto.DepositRequest;
import com.example.accountservice.dto.TransferRequest;
import com.example.accountservice.dto.WithdrawRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.entity.enums.AccountType;
import com.example.accountservice.entity.enums.TransactionStatus;
import com.example.accountservice.entity.enums.TransactionType;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.service.TransactionService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private static final int MAX_RETRIES = 3;

    private final TransactionRepository txRepo;
    private final AccountRepository accountRepo;

    @Override
    @Transactional
    public Transaction deposit(DepositRequest req) {
        // Idempotency check (fast path)
        Transaction existing = txRepo.findByIdempotencyKey(req.getIdempotencyKey()).orElse(null);
        if (existing != null) return existing;

        int attempts = 0;
        while (true) {
            try {

                Account acc = accountRepo.findByAccountNumber(req.getAccountNumber())
                        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

                // update balance
                acc.setBalance(acc.getBalance().add(req.getAmount()));
                accountRepo.saveAndFlush(acc); // triggers @Version check

                Transaction tx = Transaction.builder()
                        .toaccount(acc)
                        .transactionId(UUID.randomUUID().toString())
                        .type(TransactionType.CREDIT)
                        .amount(req.getAmount())
                        .status(TransactionStatus.SUCCESS)
                        .referenceNumber(null)
                        .fromaccount(null)
                        .transactionSource(req.getTransactionSource())
                        .description(req.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .createdBy(req.getCreatedBy())
                        .idempotencyKey(req.getIdempotencyKey())
                        .build();

                return txRepo.save(tx);

            } catch (OptimisticLockException e) {
                if (++attempts >= MAX_RETRIES) throw e;
            } catch (DataIntegrityViolationException e) {
                // handle race on idempotency unique constraint
                return txRepo.findByIdempotencyKey(req.getIdempotencyKey())
                        .orElseThrow(() -> e);
            }
        }
    }

    @Override
    @Transactional
    public Transaction withdraw(WithdrawRequest req) {
        // Idempotency
        Transaction existing = txRepo.findByIdempotencyKey(req.getIdempotencyKey()).orElse(null);
        if (existing != null) return existing;

        int attempts = 0;
        while (true) {
            try {
                Account acc = accountRepo.findByAccountNumber(req.getAccountNumber())
                        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

                if (acc.getBalance().compareTo(req.getAmount()) < 0) {
                    throw new IllegalStateException("Insufficient balance");
                }

                acc.setBalance(acc.getBalance().subtract(req.getAmount()));
                accountRepo.saveAndFlush(acc);

                Transaction tx = Transaction.builder()
                        .fromaccount(acc)
                        .toaccount(null)
                        .transactionSource(req.getTransactionSource())
                        .transactionId(UUID.randomUUID().toString())
                        .type(TransactionType.DEBIT)
                        .amount(req.getAmount())
                        .status(TransactionStatus.SUCCESS)
                        .description(req.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .createdBy(req.getCreatedBy())
                        .idempotencyKey(req.getIdempotencyKey())
                        .build();

                return txRepo.save(tx);

            } catch (OptimisticLockException e) {
                if (++attempts >= MAX_RETRIES) throw e;
            } catch (DataIntegrityViolationException e) {
                return txRepo.findByIdempotencyKey(req.getIdempotencyKey())
                        .orElseThrow(() -> e);
            }
        }
    }

    @Override
    @Transactional
    public Transaction transfer(TransferRequest req) {
        // Idempotency: one key for the whole transfer
        Transaction existing = txRepo.findByIdempotencyKey(req.getIdempotencyKey()).orElse(null);
        if (existing != null) return existing; // return the TRANSFER_OUT record

        int attempts = 0;
        while (true) {
            try {
                Account from = accountRepo.findByAccountNumber(req.getFromAccountNumber())
                        .orElseThrow(() -> new IllegalArgumentException("From account not found"));
                Account to = accountRepo.findByAccountNumber(req.getToAccountNumber())
                        .orElseThrow(() -> new IllegalArgumentException("To account not found"));

                if (from.getId().equals(to.getId())) {
                    throw new IllegalArgumentException("Cannot transfer to same account");
                }
                if (from.getBalance().compareTo(req.getAmount()) < 0) {
                    throw new IllegalStateException("Insufficient balance");
                }

                // debit from, credit to (optimistic lock will guard each row)
                from.setBalance(from.getBalance().subtract(req.getAmount()));
                to.setBalance(to.getBalance().add(req.getAmount()));

                accountRepo.saveAndFlush(from);
                accountRepo.saveAndFlush(to);

                String commonTxnId = UUID.randomUUID().toString();

                // OUT record
                Transaction out = txRepo.save(Transaction.builder()
                        .fromaccount(from)
                        .toaccount(to)
                        .transactionSource(req.getTransactionSource())
                        .transactionId(commonTxnId)
                        .type(TransactionType.DEBIT)
                        .amount(req.getAmount())
                        .status(TransactionStatus.SUCCESS)
                        .description(req.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .createdBy(req.getCreatedBy())
                        .idempotencyKey(req.getIdempotencyKey())
                        .build());

                // IN record
                txRepo.save(Transaction.builder()
                        .fromaccount(from)
                        .toaccount(to)
                        .transactionId(commonTxnId)
                        .type(TransactionType.CREDIT)
                        .amount(req.getAmount())
                        .status(TransactionStatus.SUCCESS)
                        .description(req.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .createdBy(req.getCreatedBy())
                        .idempotencyKey(req.getIdempotencyKey() + ":IN") // make unique as well
                        .build());

                return out;

            } catch (OptimisticLockException e) {
                if (++attempts >= MAX_RETRIES) throw e;
            } catch (DataIntegrityViolationException e) {
                // If duplicate idempotency happens, return existing
                return txRepo.findByIdempotencyKey(req.getIdempotencyKey())
                        .orElseThrow(() -> e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> listByAccount(Long fromaccount,Long toaccount, int page, int size) {
        return txRepo.findByFromaccount_IdOrToaccount_IdOrderByTransactionDateDesc(fromaccount,toaccount);
        // For real paging: txRepo.findByAccount_Id( accountId, PageRequest.of(page, size) )
    }
}

