package com.example.accountservice.controller;

import com.example.accountservice.dto.DepositRequest;
import com.example.accountservice.dto.TransferRequest;
import com.example.accountservice.dto.WithdrawRequest;
import com.example.accountservice.entity.Transaction;
import com.example.accountservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@RequestBody DepositRequest req) {
        return ResponseEntity.ok(transactionService.deposit(req));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestBody WithdrawRequest req) {
        return ResponseEntity.ok(transactionService.withdraw(req));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest req) {
        return ResponseEntity.ok(transactionService.transfer(req));
    }

//    @GetMapping("/account/{accountId}")
//    public ResponseEntity<List<Transaction>> listByAccount(@PathVariable Long accountId,
//                                                           @RequestParam(defaultValue = "0") int page,
//                                                           @RequestParam(defaultValue = "20") int size) {
//        return ResponseEntity.ok(transactionService.listByAccount(accountId, page, size));
//    }
}
