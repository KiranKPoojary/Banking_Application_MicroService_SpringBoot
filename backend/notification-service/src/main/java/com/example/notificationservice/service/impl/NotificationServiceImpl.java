package com.example.notificationservice.service.impl;

import com.example.notificationservice.client.UserClient;
import com.example.notificationservice.dto.AccountOpenedEvent;
import com.example.notificationservice.dto.TransactionEvent;
import com.example.notificationservice.dto.UserDto;
import com.example.notificationservice.dto.UserRegisteredEvent;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.enums.NotificationStatus;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.emailtemplate.EmailTemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private  final EmailService emailService;

    private final UserClient userClient;


    private final EmailTemplateService emailTemplateService;

    @Override
    public List<Notification> getAllNotification() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }


    @Override
    public void processUserRegisteredEvent(UserRegisteredEvent event) {
        String subject = "Welcome " + event.getUsername();
        String body = emailTemplateService.getUserRegisteredTemplate(event);
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(event.getEmail());
        notification.setSubject(subject);
        notification.setMessage(body);
        notification.setStatus(NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);

        try {
            boolean issent = emailService.sendEmail(event.getEmail(), subject, body);
            if (issent) {
                notification.setStatus(NotificationStatus.SENT);
            } else {
                notification.setStatus(NotificationStatus.FAILED);
            }
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        // Update the record in DB with final status
        notificationRepository.save(notification);
    }

    @Override
    public void processAccountOpenedEvent(AccountOpenedEvent event) {
        //calling user service to fetch user details using user id
        if(event.getUserId()==null)
        {
            throw new RuntimeException("User not found");
        }
        UserDto user = userClient.getUserById(event.getUserId(),"Kiran@1234_Notification");
        String subject = "Account Opened";
        String body =emailTemplateService.getAccountOpenedTemplate(event,user);
        String email = user.getEmail();
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(email);
        notification.setSubject(subject);
        notification.setMessage(body);
        notification.setStatus(NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);
        try {
            boolean issent = emailService.sendEmail(email, subject, body);
            if (issent) {
                notification.setStatus(NotificationStatus.SENT);
            } else {
                notification.setStatus(NotificationStatus.FAILED);
            }
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        // Update the record in DB with final status
        notificationRepository.save(notification);
    }

    @Override
    public void processTransactionEvent(TransactionEvent event) {
        //calling user-service to fetch user details
        if(event.getUserId()==null)
        {
            throw new RuntimeException("User not found");
        }
        UserDto user = userClient.getUserById(event.getUserId(),"Kiran@1234_Notification");
        String subject = "Transaction Alert";
        String body = emailTemplateService.getTransactionTemplate(event,user);
        String email= user.getEmail();
        Notification notification = new Notification();
        notification.setUserId(event.getUserId());
        notification.setEmail(email);
        notification.setSubject(subject);
        notification.setMessage(body);
        notification.setStatus(NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);
        try {
            boolean issent = emailService.sendEmail(email, subject, body);
            if (issent) {
                notification.setStatus(NotificationStatus.SENT);
            } else {
                notification.setStatus(NotificationStatus.FAILED);
            }
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
        }

        // Update the record in DB with final status
        notificationRepository.save(notification);
    }

}

