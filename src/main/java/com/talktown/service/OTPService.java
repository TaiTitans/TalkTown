package com.talktown.service;

import com.talktown.config.RedisConfig;
import com.talktown.entity.User;
import com.talktown.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    private final Jedis jedis;
    private final long OTP_EXPIRATION_MINUTES = 3;
    private final long OTP_REQUEST_EXPIRATION_MINUTES = 60; // 1 hour
    private final int MAX_OTP_SEND_COUNT = 5;
    public OTPService(RedisConfig redisConfig) {
        this.jedis = redisConfig.jedisClient();
    }

    public String generateOTP() {
        // Tạo mã OTP ngẫu nhiên, ví dụ: 123456
        return String.format("%06d", new Random().nextInt(900000) + 100000);
    }

    public void saveOTP(String email, String otp) {
        // Lưu mã OTP và thời gian hết hạn vào Redis
        jedis.setex(email, (int) TimeUnit.MINUTES.toSeconds(OTP_EXPIRATION_MINUTES), otp);
    }
    public String getOTPFromRedis(String email) {
        return jedis.get(email);
    }

    public void sendOtp(String email, String requestId) {
        User existingUserEmail = userRepository.findByEmail(email);
        if (existingUserEmail != null) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Check OTP send count for the requestId
        String otpSendCountKey = "otp_send_count_" + requestId;
        long otpSendCount = jedis.incr(otpSendCountKey);
        if (otpSendCount > MAX_OTP_SEND_COUNT) {
            throw new IllegalArgumentException("Maximum number of OTP send requests reached");
        }

        // Generate and save OTP
        String otp = generateOTP();
        saveOTP(email, otp);

        // Send OTP to email
        emailService.sendOTPEmail(email, otp);

        // Set expiration for the OTP send count
        jedis.expire(otpSendCountKey, (int) TimeUnit.HOURS.toSeconds(1));
    }

    public boolean isOTPValid(String email, String otp) {
        // Kiểm tra xem mã OTP có hợp lệ và chưa hết hạn không
        String storedOtp = jedis.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            return true;
        }
        return false;
    }
}