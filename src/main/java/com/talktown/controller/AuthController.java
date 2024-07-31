package com.talktown.controller;

import com.talktown.common.SendOTPRequest;
import com.talktown.common.StatusResponse;
import com.talktown.config.SecurityConfig;
import com.talktown.dto.EmailDTO;
import com.talktown.service.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private static final Logger log =  LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private OTPService otpService;

    @PostMapping("/otp/signup")
    public ResponseEntity<StatusResponse<String>> sendOTPRegister(@RequestParam String email, HttpServletRequest request) {
        try {
            otpService.sendOTPForRegistration(email, request);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Send OTP Success", null));
        } catch (IllegalArgumentException e) {
            log.error("Error sending OTP: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending OTP", e);
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred while sending the OTP.", null));
        }
    }

    @PostMapping("/otp/email")
    public ResponseEntity<StatusResponse<String>> sendOTPForChangeEmail(@RequestParam String currentEmail, @RequestParam String newEmail, HttpServletRequest request) {
        try {
            otpService.sendOTPForChangeEmail(currentEmail, newEmail, request);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Send OTP Success", null));
        } catch (IllegalArgumentException e) {
            log.error("Error sending OTP: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending OTP", e);
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred while sending the OTP.", null));
        }
    }
    @PostMapping("/otp/forgot")
    public ResponseEntity<StatusResponse<String>> sendOTPForForgotPassword(@RequestParam String email, HttpServletRequest request) {
        try {
            otpService.sendOTPForForgotPassword(email, request);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Send OTP Success", null));
        } catch (IllegalArgumentException e) {
            log.error("Error sending OTP: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending OTP", e);
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred while sending the OTP.", null));
        }
    }
}
