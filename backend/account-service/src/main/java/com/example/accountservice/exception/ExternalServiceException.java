package com.example.accountservice.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String s) {
        super(s);
    }
}
