package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.TransactionSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private String accountNumber;
    private BigDecimal amount;
    private TransactionSource transactionSource;
    private String description;
    private String idempotencyKey;
    private String createdBy;
}
