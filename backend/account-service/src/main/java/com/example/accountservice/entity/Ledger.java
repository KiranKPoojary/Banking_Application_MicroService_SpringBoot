package com.example.accountservice.entity;


import com.example.accountservice.entity.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ledger",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"idempotencyKey", "entryType"})
        },
        indexes = {
                @Index(name = "idx_ledger_account_date", columnList = "account_id, entry_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Long id;

    /** Which account this entry belongs to */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /** Debit or Credit */
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 20)
    private TransactionType entryType; // DEBIT, CREDIT

    /** Monetary value */
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    private String description;

    /** Link to the transaction (business event) that generated this entry */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "txn_id", nullable = false)
    private Transaction transaction;

    /** For idempotency (same request won’t insert twice) */
    @Column(name = "idempotencyKey", length = 64, nullable = false, updatable = false)
    private String idempotencyKey;

    /** Entry timestamp */
    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

}

