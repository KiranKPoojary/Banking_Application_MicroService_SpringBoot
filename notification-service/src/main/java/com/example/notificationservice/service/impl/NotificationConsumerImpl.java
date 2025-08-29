package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.service.NotificationConsumer;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumerImpl implements NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-registered", groupId = "notification-service")
    public void consume(String jsonEvent) {
        UserRegisteredEvent event = null;
        try {
            event = new ObjectMapper().readValue(jsonEvent, UserRegisteredEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received UserRegisteredEvent: " + event);
        notificationService.processUserRegisteredEvent(event);
    }
}
