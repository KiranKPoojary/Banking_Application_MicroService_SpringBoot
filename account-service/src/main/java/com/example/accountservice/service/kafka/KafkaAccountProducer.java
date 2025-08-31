package com.example.accountservice.service.kafka;

import com.example.accountservice.dto.AccountOpenedEvent;
import com.example.accountservice.dto.TransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaAccountProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public KafkaAccountProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendAccountOpenedEvent(AccountOpenedEvent event) {
        sendMessage("account-opened-events", event);
    }

    public void sendTransactionEvent(TransactionEvent event) {
        sendMessage("transaction-events", event);
    }

   public void sendMessage(String topic, Object event) {
       try {
           String json = objectMapper.writeValueAsString(event);
           kafkaTemplate.send(topic, json);
           System.out.println("Sent event to topic [" + topic + "]: " + json);
       } catch (JsonProcessingException e) {
           throw new RuntimeException("Error serializing event", e);
       }
   }
}
