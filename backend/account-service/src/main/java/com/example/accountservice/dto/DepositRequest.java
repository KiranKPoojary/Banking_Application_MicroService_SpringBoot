package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.TransactionSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    private String accountNumber;
    private BigDecimal amount;
    private String description;
    private TransactionSource transactionSource;
    private String idempotencyKey; // client-sent UUID
    private String createdBy;      // user/system
}
