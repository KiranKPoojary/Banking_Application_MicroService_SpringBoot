package com.example.accountservice.service.implement;

import com.example.accountservice.dto.DepositRequest;
import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.dto.TransferRequest;
import com.example.accountservice.dto.WithdrawRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Ledger;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.entity.enums.TransactionStatus;
import com.example.accountservice.entity.enums.TransactionType;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.LedgerRepository;
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
    private final LedgerRepository ledgerRepo;

    private static final String BANK_CASH_ACCOUNT_NO = "BANK_CASH";

    private Account getBankCashAccount() {
        return accountRepo.findByAccountNumber(BANK_CASH_ACCOUNT_NO)
                .orElseThrow(() -> new IllegalStateException("Bank cash account missing"));
    }

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

                Account BANK_CASH_VAULT = getBankCashAccount();

                // update balance
                acc.setBalance(acc.getBalance().add(req.getAmount()));
                accountRepo.saveAndFlush(acc); // triggers @Version check

                //update bank_cash_vault
                BANK_CASH_VAULT.setBalance(BANK_CASH_VAULT.getBalance().add(req.getAmount()));
                accountRepo.saveAndFlush(BANK_CASH_VAULT);


                //transaction entry
                Transaction tx = Transaction.builder()
                        .toaccount(acc)
                        .transactionId(UUID.randomUUID().toString())
                        .type(TransactionType.DEPOSIT)
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

                Transaction savedTx = txRepo.saveAndFlush(tx);

                // create ledger entries (double-entry)
                //CREDIT ENTRY
                Ledger creditEntry = Ledger.builder()
                        .transaction(savedTx)
                        .account(acc)
                        .idempotencyKey(req.getIdempotencyKey())
                        .entryType(TransactionType.CREDIT)
                        .description(req.getDescription())
                        .amount(req.getAmount())
                        .entryDate(LocalDateTime.now())
                        .build();

                //DEBIT ENTRY
                Ledger debitEntry = Ledger.builder()
                        .transaction(savedTx)
                        .account(BANK_CASH_VAULT) // pseudo account representing the bank
                        .entryType(TransactionType.DEBIT)
                        .description(req.getDescription())
                        .idempotencyKey(req.getIdempotencyKey())
                        .amount(req.getAmount())
                        .entryDate(LocalDateTime.now())
                        .build();

            savedTx.getLedgerEntries().add(debitEntry);
            savedTx.getLedgerEntries().add(creditEntry);
//                ledgerRepo.saveAll(Arrays.asList(creditEntry, debitEntry));

                return savedTx;

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

                Account BANK_CASH_VAULT = getBankCashAccount();

                //update user account
                acc.setBalance(acc.getBalance().subtract(req.getAmount()));
                accountRepo.saveAndFlush(acc);

                //update bank_cash_vault
                BANK_CASH_VAULT.setBalance(BANK_CASH_VAULT.getBalance().subtract(req.getAmount()));
                accountRepo.saveAndFlush(BANK_CASH_VAULT);

                Transaction tx = Transaction.builder()
                        .fromaccount(acc)
                        .toaccount(null)
                        .transactionSource(req.getTransactionSource())
                        .transactionId(UUID.randomUUID().toString())
                        .type(TransactionType.WITHDRAWAL)
                        .amount(req.getAmount())
                        .status(TransactionStatus.SUCCESS)
                        .description(req.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .createdBy(req.getCreatedBy())
                        .idempotencyKey(req.getIdempotencyKey())
                        .build();

                Transaction savedTx = txRepo.saveAndFlush(tx);

                // create ledger entries (double-entry)
                // Debit entry
                Ledger debitEntry = Ledger.builder()
                        .transaction(savedTx)
                        .account(acc) // pseudo account representing the bank
                        .entryType(TransactionType.DEBIT)
                        .idempotencyKey(req.getIdempotencyKey())
                        .description(req.getDescription())
                        .amount(req.getAmount())
                        .entryDate(LocalDateTime.now())
                        .build();

                //Credit Entry
                Ledger creditEntry = Ledger.builder()
                        .transaction(savedTx)
                        .account(BANK_CASH_VAULT)
                        .entryType(TransactionType.CREDIT)
                        .idempotencyKey(req.getIdempotencyKey())
                        .description(req.getDescription())
                        .amount(req.getAmount())
                        .entryDate(LocalDateTime.now())
                        .build();

                savedTx.getLedgerEntries().add(debitEntry);
                savedTx.getLedgerEntries().add(creditEntry);

                return savedTx;

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

                if (from == null || to == null) {
                    throw new IllegalArgumentException("Accounts cannot be null");
                }

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

                // OUT record-Transaction Record
                Transaction TX = txRepo.save(Transaction.builder()
                        .fromaccount(from)
                        .toaccount(to)
                        .transactionSource(req.getTransactionSource())
                        .transactionId(commonTxnId)
                        .type(TransactionType.TRANSFER)
                        .amount(req.getAmount())
                        .status(TransactionStatus.SUCCESS)
                        .description(req.getDescription())
                        .transactionDate(LocalDateTime.now())
                        .createdBy(req.getCreatedBy())
                        .idempotencyKey(req.getIdempotencyKey())
                        .build());

//                // IN record
//                txRepo.save(Transaction.builder()
//                        .fromaccount(from)
//                        .toaccount(to)
//                        .transactionId(commonTxnId)
//                        .type(TransactionType.CREDIT)
//                        .amount(req.getAmount())
//                        .status(TransactionStatus.SUCCESS)
//                        .description(req.getDescription())
//                        .transactionDate(LocalDateTime.now())
//                        .createdBy(req.getCreatedBy())
//                        .idempotencyKey(req.getIdempotencyKey()) // make unique as well
//                        .build());


                // CREATE ledger entries (double-entry)
                // Debit entry
                Ledger debitEntry = Ledger.builder()
                        .transaction(TX)
                        .account(from)
                        .entryType(TransactionType.DEBIT)
                        .idempotencyKey(req.getIdempotencyKey())
                        .description(req.getDescription())
                        .amount(req.getAmount())
                        .entryDate(LocalDateTime.now())
                        .build();

                //Credit Entry
                Ledger creditEntry = Ledger.builder()
                        .transaction(TX)
                        .account(to)
                        .entryType(TransactionType.CREDIT)
                        .idempotencyKey(req.getIdempotencyKey())
                        .description(req.getDescription())
                        .amount(req.getAmount())
                        .entryDate(LocalDateTime.now())
                        .build();

                TX.getLedgerEntries().add(debitEntry);
                TX.getLedgerEntries().add(creditEntry);

                return TX;

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
    public List<LedgerDto> listByAccount(Long accountId, int page, int size) {
        return ledgerRepo.findByAccount_IdOrderByEntryDateDesc(accountId)
                .stream()
                .map(l -> new LedgerDto(l.getTransaction().getTransactionId(),l.getEntryType(),l.getAmount(),l.getDescription(),l.getTransaction().getTransactionDate()))
                .toList();

        // For real paging: txRepo.findByAccount_Id( accountId, PageRequest.of(page, size) )
    }
}

