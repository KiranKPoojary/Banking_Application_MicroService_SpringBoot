package com.example.accountservice.entity;


import com.example.accountservice.entity.enums.AccountType;
import com.example.accountservice.entity.enums.RequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // who requested

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 50)
    private AccountType accountType; // Savings, Current, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status; // REQUESTED, CREATED, APPROVED, REJECTED

    @Column(name = "account_number", unique = true, length = 30)
    private String accountNumber; // null until executive assigns



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    // Expose only accountId in JSON
    @JsonProperty("accountId")
    public Long getAccountId() {
        return account != null ? account.getId() : null;
    }


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // getters, setters, constructors
}

