package com.tutorflow.parentservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendProgressReport(String toEmail, String studentName, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Tutorflow Progress Report" + studentName);
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Progress report sent to {}", toEmail);
        } catch(Exception e) {
          log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
