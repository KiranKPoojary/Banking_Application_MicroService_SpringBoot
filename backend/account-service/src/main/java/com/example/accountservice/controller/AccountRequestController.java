package com.example.accountservice.controller;


import com.example.accountservice.entity.AccountRequest;
import com.example.accountservice.entity.CustomUserDetails;
import com.example.accountservice.entity.enums.RequestStatus;
import com.example.accountservice.repository.AccountRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v0/account-request")
public class AccountRequestController {

    private final AccountRequestRepository accountRequestRepository;

    public AccountRequestController(AccountRequestRepository accountRequestRepository) {
        this.accountRequestRepository = accountRequestRepository;
    }

    //User creates request
    @PostMapping
    public ResponseEntity<AccountRequest> createRequest(@RequestBody AccountRequest req, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        req.setStatus(RequestStatus.REQUESTED);
        req.setCreatedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());
        req.setUserId(userDetails.getId());
        return ResponseEntity.ok(accountRequestRepository.save(req));
    }


    @GetMapping("/all")
    public ResponseEntity<List<AccountRequest>> getAllRequests(Authentication authentication) {
        return ResponseEntity.ok(accountRequestRepository.findAll());
    }

    @GetMapping
    public ResponseEntity<List<AccountRequest>> getAllRequestsByUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(accountRequestRepository.findByUserId(userDetails.getId()));
    }
}
