package com.talktown.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
public void sendOTPEmail(String to, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("talktowntechnology@gmail.com");
        message.setTo(to);
        message.setSubject("TalkTown Verify OTP");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
}
}
