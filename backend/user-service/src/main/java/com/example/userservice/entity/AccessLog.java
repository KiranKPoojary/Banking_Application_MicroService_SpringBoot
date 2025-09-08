package com.example.userservice.entity;



import com.example.userservice.entity.enums.UserAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="user_logs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessLog{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

//    @ManyToOne(fetch = FetchType.LAZY) // Many logs can belong to one user
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @Column(name="username")
    private String username;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "action",nullable = false)
    @Enumerated(EnumType.STRING)
    private UserAction action; // e.g., LOGIN, LOGOUT, PASSWORD_CHANGE, PROFILE_UPDATE

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}