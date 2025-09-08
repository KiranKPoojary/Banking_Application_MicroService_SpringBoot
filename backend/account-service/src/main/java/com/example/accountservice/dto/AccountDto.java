package com.example.accountservice.dto;

import com.example.accountservice.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDto {
    private Long userId;
    private String accountNumber;
    private AccountType AccountType;
    private Long createdBy;

}
