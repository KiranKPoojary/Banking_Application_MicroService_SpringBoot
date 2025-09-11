package com.example.notificationservice.service;

import com.example.notificationservice.dto.AccountOpenedEvent;
import com.example.notificationservice.dto.TransactionEvent;
import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.entity.Notification;

import java.util.List;

public interface NotificationService {

//    Notification sendNotification(Notification notification);

//    Notification sendWelcomeNotification(Notification notification);

    List<Notification> getAllNotification();
    Notification getNotificationById(Long id);

    List<Notification> getNotificationsByUserId(Long userId);

    void processUserRegisteredEvent(UserRegisteredEvent event);

    void processAccountOpenedEvent(AccountOpenedEvent event);

    void processTransactionEvent(TransactionEvent event);

}
