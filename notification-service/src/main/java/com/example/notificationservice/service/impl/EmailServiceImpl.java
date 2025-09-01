package com.example.notificationservice.service.impl;

import com.example.notificationservice.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }


    public boolean sendEmail(String to, String subject, String body) {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);  // ✅ second param = true → HTML mode

            mailSender.send(mimeMessage);

            return true;
        } catch (Exception e) {
            System.out.printf(e.getMessage());
            return false;
        }

    }
}
