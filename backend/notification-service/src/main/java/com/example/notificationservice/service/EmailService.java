package com.example.notificationservice.service;

public interface EmailService {

    boolean sendEmail(String to, String subject, String body);
}
