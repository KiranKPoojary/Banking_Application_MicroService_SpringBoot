package com.example.notificationservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountOpenedEvent {
    private Long accountId;
    private String accountNumber;
    private Long userId;      // ID from user-service
    private String accountType; // e.g., SAVINGS, CURRENT
    private BigDecimal initialBalance;
    private LocalDateTime openedAt;
}
