package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.TransactionType;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor

public class LedgerDto {
//    private Long ledgerId;

//    private Long accountId;   // From Account entity

    private String transactionId; // From Transaction entity

    private TransactionType entryType; // DEBIT or CREDIT

    private BigDecimal amount;

    private String description;

    private LocalDateTime entryDate;

}
