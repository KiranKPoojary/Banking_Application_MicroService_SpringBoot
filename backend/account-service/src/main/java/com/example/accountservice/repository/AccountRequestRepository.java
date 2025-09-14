package com.example.accountservice.repository;

import com.example.accountservice.entity.AccountRequest;
import com.example.accountservice.entity.enums.AccountType;
import com.example.accountservice.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
    List<AccountRequest> findByUserId(Long userId);

    Optional<AccountRequest> findByUserIdAndAccountTypeAndStatus(Long userId, AccountType accountType, RequestStatus status);
}
