package com.example.accountservice.service;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.Ledger;
import com.example.accountservice.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> getAllAccount();
//    Optional<Account> getAccountByName(String name);
    List<LedgerDto> getTransactionsForAccount(Long accountId);
    Account createAccount(AccountDto account);
    Account approveAccount(Long accountId,Long EmployeeId);
    Optional<Account> getAccountById(Long id);
    List<Account> getAccountsByUserId(Long userId);
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id);


//    Account findById(Long id);
}
