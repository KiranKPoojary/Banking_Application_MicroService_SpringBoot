package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private Long accountId;
    private String accountNumber;
    private Long userId;     // ID from user-service
    private Double amount;
    private TransactionType type;       // DEPOSIT or WITHDRAWAL
    private String status;     // SUCCESS or FAILED
    private LocalDateTime transactionAt; // timestamp
    private String description;
    private BigDecimal balance;
}
