package com.talktown.repository;

public interface EmailSender {
    void sendOTPEmail(String to, String otp);
}
