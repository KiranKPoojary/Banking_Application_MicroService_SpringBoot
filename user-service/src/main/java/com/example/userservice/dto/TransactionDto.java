package com.example.userservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDto {
    private String transactionId;
    private String type; // CREDIT DEBIT
    private Double amount;
    private String description;
    private LocalDateTime timestamp;
}
