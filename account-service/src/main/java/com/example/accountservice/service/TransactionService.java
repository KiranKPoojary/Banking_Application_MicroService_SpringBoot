package com.example.accountservice.service;

import com.example.accountservice.dto.DepositRequest;
import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.dto.TransferRequest;
import com.example.accountservice.dto.WithdrawRequest;
import com.example.accountservice.entity.Transaction;

import java.util.List;


public interface TransactionService {
    Transaction deposit(DepositRequest req);
    Transaction withdraw(WithdrawRequest req);
    // Returns the "TRANSFER_OUT" from source account; you'll also have a matching TRANSFER_IN
    Transaction transfer(TransferRequest req);

    List<LedgerDto> listByAccount(Long accountId, int page, int size);
}
