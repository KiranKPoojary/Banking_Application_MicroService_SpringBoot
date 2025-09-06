package com.example.accountservice.service.implement;

import com.example.accountservice.client.UserClient;
import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.dto.AccountOpenedEvent;
import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.dto.UserDto;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.CustomUserDetails;
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
import com.example.accountservice.service.kafka.KafkaAccountProducer;
import feign.FeignException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserClient userClient;
    private final TransactionService transactionService;


    private final KafkaAccountProducer kafkaAccountProducer;

//    public AccountServiceImpl(AccountRepository accountRepository, UserClient userClient, TransactionService transactionService, AccountOpenedEvent accountOpenedEvent, KafkaAccountProducer kafkaAccountProducer) {
//        this.accountRepository = accountRepository;
//        this.userClient = userClient;
//        this.transactionService = transactionService;
//        this.accountOpenedEvent = accountOpenedEvent;
//        this.kafkaAccountProducer = kafkaAccountProducer;
//    }

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
            account.setStatus(AccountStatus.PENDING_APPROVAL);
            account.setBalance(BigDecimal.ZERO);
            account.setUpdatedAt(LocalDateTime.now());
            account.setCreatedBy(request.getCreatedBy());
            Account newAccount= accountRepository.save(account);

// ***********This is not called - Kafka called when account gets approved by Manager

//            // Call Kafka Producer to push notification On successful open of account
//            AccountOpenedEvent accountOpenedEvent = new AccountOpenedEvent();
//            accountOpenedEvent.setUserId(newAccount.getUserId());
//            accountOpenedEvent.setAccountId(newAccount.getId());
//            accountOpenedEvent.setAccountNumber(newAccount.getAccountNumber());
//            accountOpenedEvent.setAccountType(newAccount.getAccountType());
//            accountOpenedEvent.setInitialBalance(newAccount.getBalance());
//            accountOpenedEvent.setOpenedAt(newAccount.getCreatedAt());
//
//            //Calling Kafka Produce method
//            kafkaAccountProducer.sendAccountOpenedEvent(accountOpenedEvent);

            return newAccount;

        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User not found with id: " + request.getUserId());
        } catch (FeignException ex) {
            throw new ExternalServiceException("User service unavailable, please try again later");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }

    //Approve Account
    @Override
    public Account approveAccount(Long id,Long EmployeeId){
        Account account=accountRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Account not found with id: " + id));
        account.setApprovedBy(EmployeeId);
        account.setUpdatedAt(LocalDateTime.now());
        account.setStatus(AccountStatus.ACTIVE);

        Account approvedAccount= accountRepository.save(account);
        // Call Kafka Producer to push notification On successful open of account
        AccountOpenedEvent accountOpenedEvent = new AccountOpenedEvent();
        accountOpenedEvent.setUserId(approvedAccount.getUserId());
        accountOpenedEvent.setAccountId(approvedAccount.getId());
        accountOpenedEvent.setAccountNumber(approvedAccount.getAccountNumber());
        accountOpenedEvent.setAccountType(approvedAccount.getAccountType());
        accountOpenedEvent.setInitialBalance(approvedAccount.getBalance());
        accountOpenedEvent.setOpenedAt(approvedAccount.getCreatedAt());

        //Calling Kafka Produce method
        kafkaAccountProducer.sendAccountOpenedEvent(accountOpenedEvent);

        return approvedAccount;
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
