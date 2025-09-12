package com.example.accountservice.repository;

import com.example.accountservice.entity.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
    List<AccountRequest> findByUserId(Long userId);
}
