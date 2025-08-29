package com.example.userservice.service.implement;

import com.example.userservice.dto.UserRegisteredEvent;
import com.example.userservice.service.UserRegisteredEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisteredEventProducerImpl implements UserRegisteredEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonEvent;
        try {
            jsonEvent = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("user-registered", jsonEvent);
    }
}
