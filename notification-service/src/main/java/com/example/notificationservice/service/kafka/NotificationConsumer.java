package com.example.notificationservice.service.kafka;

import com.example.notificationservice.dto.AccountOpenedEvent;
import com.example.notificationservice.dto.TransactionEvent;
import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @KafkaListener(topics = "user-registered", groupId = "notification-service")
    public void consumeUserRegistered(String message) {
        try {
            UserRegisteredEvent event = objectMapper.readValue(message, UserRegisteredEvent.class);
            System.out.println("Received UserRegisteredEvent: " + event);
            notificationService.processUserRegisteredEvent(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "account-opened-events", groupId = "notification-service")
    public void consumeAccountOpened(String message) {
        try {
            AccountOpenedEvent event = objectMapper.readValue(message, AccountOpenedEvent.class);
            notificationService.processAccountOpenedEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "transaction-events", groupId = "notification-service")
    public void consumeTransactionEvent(String message) {
        try {
            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);
            notificationService.processTransactionEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
