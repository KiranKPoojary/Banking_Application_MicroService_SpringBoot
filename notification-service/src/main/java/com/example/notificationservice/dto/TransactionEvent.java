package com.example.notificationservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionEvent {
    private String transactionId;
    private Long accountId;
    private String accountNumber;
    private Long userId;     // ID from user-service
    private Double amount;
    private String type;       // DEPOSIT or WITHDRAWAL
    private String status;     // SUCCESS or FAILED
    private LocalDateTime transactionAt; // timestamp
    private String description;
}
