package com.example.accountservice.entity;

import com.example.accountservice.entity.enums.TransactionSource;
import com.example.accountservice.entity.enums.TransactionStatus;
import com.example.accountservice.entity.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(name = "transactions",
//        indexes = {
////                @Index(name = "idx_txn_account_date", columnList = "account_id, transaction_date"),
////                @Index(name = "idx_txn_ext_id", columnList = "idempotency_key")
//        },
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_idempotency_key", columnNames = {"idempotency_key","type"})
//        })

@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"idempotencyKey", "type"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "txn_id")
    private Long id;

    // Owning account for single-leg ops (deposit/withdraw).
    // For transfers we'll create TWO records (one for each account).

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "from_account_id", nullable = true)
    private Account fromaccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "to_account_id", nullable = true)
    private Account toaccount;

    @Enumerated(EnumType.STRING)
    @Column(name="transaction_source")
    private TransactionSource transactionSource;

    @Column(name = "transaction_id", nullable = false, updatable = false, length = 50,unique = true)
    private String transactionId; // internal UUID for traceability

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type; // TRANSFER,DEPOSIT,WITHDRAWAL

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status; // PENDING, SUCCESS, FAILED

    @Column(name = "reference_number", length = 64)
    private String referenceNumber; // external rails ref (UPI/NEFT/etc)


    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "created_by", length = 64, nullable = false)
    private String createdBy;

    @Column(name = "idempotency_key", length = 64, nullable = false, updatable = false)
    private String idempotencyKey; // prevent duplicates (one per client request)

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;


    @JsonManagedReference
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ledger> ledgerEntries = new ArrayList<>();
}
