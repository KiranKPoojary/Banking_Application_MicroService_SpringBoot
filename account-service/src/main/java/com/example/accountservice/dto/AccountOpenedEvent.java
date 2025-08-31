package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountOpenedEvent {
    private Long accountId;
    private String accountNumber;
    private Long userId;      // ID from user-service
    private AccountType accountType; // e.g., SAVINGS, CURRENT
    private BigDecimal initialBalance;
    private LocalDateTime openedAt;    // timestamp
}
