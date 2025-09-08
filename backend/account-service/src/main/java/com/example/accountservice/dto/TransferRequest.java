package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.TransactionSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private TransactionSource transactionSource;
    private BigDecimal amount;
    private String description;
    private String idempotencyKey; // single key for the whole transfer
    private String createdBy;
}
