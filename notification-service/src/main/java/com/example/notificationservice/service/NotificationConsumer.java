package com.example.notificationservice.service;

import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.entity.Notification;

public interface NotificationConsumer {
    void consume(String event);
}
