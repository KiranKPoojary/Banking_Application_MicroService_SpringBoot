package com.example.userservice.service;

import com.example.userservice.dto.UserRegisteredEvent;

public interface UserRegisteredEventProducer {

    void sendUserRegisteredEvent(UserRegisteredEvent event);
}
