package com.example.notificationservice.service.emailtemplate;

import com.example.notificationservice.dto.AccountOpenedEvent;
import com.example.notificationservice.dto.TransactionEvent;
import com.example.notificationservice.dto.UserDto;
import com.example.notificationservice.dto.UserRegisteredEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;


@Service
//@AllArgsConstructor
//@NoArgsConstructor
public class EmailTemplateService {

    public String getUserRegisteredTemplate(UserRegisteredEvent event) {
        return String.format("""
            <html>
              <body style="font-family: Arial, sans-serif; color: #333; line-height:1.6;">
                <h2 style="color:#2E86C1;">Welcome to Our Bank, %s %s!</h2>
                <p>Your account has been <b>successfully registered</b> with us.</p>
                <table style="border-collapse: collapse; margin-top:10px;">
                  <tr><td><b>Username:</b></td><td>%s</td></tr>
                  <tr><td><b>Email:</b></td><td>%s</td></tr>
                </table>
                <p style="margin-top:20px;">We’re excited to have you onboard. You can now explore our digital banking services.</p>
                 <p style="margin-top:20px;">Next step is to Open Your Bank Account</p>

                <p style="color:#555;">Regards,<br><b>Your Banking Team</b></p>
              </body>
            </html>
            """, event.getFirstName(), event.getLastName(),
                event.getUsername(), event.getEmail());
    }

    public String getAccountOpenedTemplate(AccountOpenedEvent event, UserDto user) {
        return String.format("""
            <html>
              <body style="font-family: Arial, sans-serif; color: #333; line-height:1.6;">
                <h2 style="color:#27AE60;">Congratulations, %s %s! 🎉</h2>
                <p>Your new bank account has been <b>successfully created</b>.</p>
                <table style="border-collapse: collapse; margin-top:10px;">
                  <tr><td><b>Account Number:</b></td><td>%s</td></tr>
                  <tr><td><b>Account Type:</b></td><td>%s</td></tr>
                  <tr><td><b>Linked User:</b></td><td>%s (%s)</td></tr>
                </table>
                <p style="margin-top:20px;">You can now start transacting securely with us using your account dashboard.</p>
                <p style="color:#555;">Warm Regards,<br><b>Your Banking Team</b></p>
              </body>
            </html>
            """, user.getFirstName(), user.getLastName(),
                event.getAccountNumber(), event.getAccountType(),
                user.getUsername(), user.getEmail());
    }

    public String getTransactionTemplate(TransactionEvent event, UserDto user) {
        String formattedDate = event.getTransactionAt()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));

        return String.format("""
            <html>
              <body style="font-family: Arial, sans-serif; color: #333; line-height:1.6;">
                <h2 style="color:#8E44AD;">Transaction Alert</h2>
                <p>Dear %s %s,</p>
                <p>A new transaction has been processed on your account:</p>
                <table style="border-collapse: collapse; margin-top:10px;">
                  <tr><td><b>Transaction ID:</b></td><td>%s</td></tr>
                  <tr><td><b>Account Number:</b></td><td>%s</td></tr>
                  <tr><td><b>Type:</b></td><td>%s</td></tr>
                  <tr><td><b>Amount:</b></td><td>%.2f</td></tr>
                  <tr><td><b>Status:</b></td><td>%s</td></tr>
                  <tr><td><b>Date:</b></td><td>%s</td></tr>
                  <tr><td><b>Description:</b></td><td>%s</td></tr>
                </table>
                <p style="margin-top:20px; color:#C0392B;">
                  ⚠️ If you did not authorize this transaction, please contact our support team immediately.
                </p>
                <p style="color:#555;">Thank you for banking with us.<br><b>Your Banking Team</b></p>
              </body>
            </html>
            """, user.getFirstName(), user.getLastName(),
                event.getTransactionId(), event.getAccountNumber(),
                event.getType(), event.getAmount(), event.getStatus(),
                formattedDate, event.getDescription());
    }
}
