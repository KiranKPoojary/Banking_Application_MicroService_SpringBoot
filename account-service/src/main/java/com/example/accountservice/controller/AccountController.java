package com.example.accountservice.controller;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.CustomUserDetails;
import com.example.accountservice.entity.Ledger;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.service.AccountService;
import com.example.accountservice.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/v0/accounts")
public class AccountController {

    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;

    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EXECUTIVE')")
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccount());
    }

    //Create Account can be done by only employee with role of executive
    @PreAuthorize("hasRole('EXECUTIVE')")
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto account) {

        // Generate account number
        if (account.getAccountNumber() == null) {
            account.setAccountNumber(String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L)));
        }
        CustomUserDetails customUserDetails=(CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        account.setCreatedBy(customUserDetails.getId());
        return ResponseEntity.ok(accountService.createAccount(account));
    }

    //Approve account can be done by only Manager
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<Account> approveAccount(@PathVariable Long id) {
        CustomUserDetails customUserDetails=(CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long employeeId=customUserDetails.getId();
        return ResponseEntity.ok(accountService.approveAccount(id,employeeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EXECUTIVE')")
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //getting transactions of a account
    @GetMapping("/{accountId}/transactions")
    public List<LedgerDto> getAccountTransactions(@PathVariable Long accountId) {
        return accountService.getTransactionsForAccount(accountId);
    }

    //getting account details of user using userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return ResponseEntity.ok(accountService.updateAccount(id, account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
