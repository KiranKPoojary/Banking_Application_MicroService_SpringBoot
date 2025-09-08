package com.example.userservice.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceKafkaConfig {

//
//    @Bean
//    public ProducerFactory<String, UserRegisteredEvent> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // ✅ important
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean
//    public KafkaTemplate<String,String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
}
