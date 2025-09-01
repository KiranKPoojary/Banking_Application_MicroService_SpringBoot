package com.example.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
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
    private String status;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String transactionAt; // timestamp
    private String description;
    private BigDecimal balance;
}
