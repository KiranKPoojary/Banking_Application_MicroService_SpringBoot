package com.example.accountservice.entity.enums;

public enum RequestStatus {
    REQUESTED,   // user submitted
    CREATED,     // executive generated account number
    APPROVED,    // manager approved
    REJECTED     // manager rejected
}
