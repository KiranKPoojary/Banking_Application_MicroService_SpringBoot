package com.example.accountservice.repository;

import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByUserId(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByUserIdAndAccountType(Long userId, AccountType accountType);
}
