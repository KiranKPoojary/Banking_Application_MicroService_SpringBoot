package com.example.accountservice.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String s) {
        super(s);
    }
}
