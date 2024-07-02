package com.talktown.service;

import com.talktown.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger =  LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender emailSender;

    public void sendOTPEmail(String to, String otp) {
        // Trim the email to remove leading/trailing spaces
        to = to.trim();

        // Validate email address
        if (to == null || !to.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Log the email address for debugging
        System.out.println("Sending OTP to email: " + to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + otp);
            emailSender.send(message);
            logger.info("OTP Sent Successfully");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new MailParseException("Could not parse mail", e);
        }
    }
}
