package com.example.accountservice.service.implement;

import com.example.accountservice.client.UserClient;
import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.dto.UserDto;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Ledger;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.entity.enums.AccountStatus;
import com.example.accountservice.exception.DuplicateAccountException;
import com.example.accountservice.exception.ExternalServiceException;
import com.example.accountservice.exception.UserNotFoundException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.service.AccountService;
import com.example.accountservice.service.TransactionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserClient userClient;
    private final TransactionService transactionService;

    public AccountServiceImpl(AccountRepository accountRepository, UserClient userClient, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.userClient = userClient;
        this.transactionService = transactionService;
    }

    @Override
    public Account createAccount(AccountDto request) {
        try {
            // Call User Service via Feign to validate user
            UserDto user = userClient.getUserById(request.getUserId());
            if (user == null) {
                throw new UserNotFoundException("User not found with id: " + request.getUserId());
            }

            // Check if user already has account of this type
            Optional<Account> existingAccount = accountRepository.findByUserIdAndAccountType(
                    request.getUserId(), request.getAccountType());

            if (existingAccount.isPresent()) {
                throw new DuplicateAccountException("User already has an account of type: " + request.getAccountType());
            }


            // Create new Account entity safely
            Account account = new Account();
            account.setUserId(request.getUserId());
            account.setAccountNumber(request.getAccountNumber());
            account.setAccountType(request.getAccountType());
            account.setCreatedAt(LocalDateTime.now());
            account.setStatus(AccountStatus.ACTIVE);
            account.setBalance(BigDecimal.ZERO);
            account.setUpdatedAt(LocalDateTime.now());
            account.setUpdatedBy(request.getUpdated_by());

            return accountRepository.save(account);

        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User not found with id: " + request.getUserId());
        } catch (FeignException ex) {
            throw new ExternalServiceException("User service unavailable, please try again later");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Account>getAllAccount(){
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    //account transactions call transaction service
    @Override
    public List<LedgerDto> getTransactionsForAccount(Long accountId) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account with the accountId not found"+ accountId));

        return transactionService.listByAccount(acc.getId(),1,2);

    }

    @Override
    public Account updateAccount(Long id, Account account) {
        return accountRepository.findById(id).map(existing -> {
            existing.setAccountType(account.getAccountType());
            existing.setBalance(account.getBalance());
            existing.setStatus(account.getStatus());
            return accountRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
