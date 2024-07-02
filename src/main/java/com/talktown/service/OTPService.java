package com.talktown.service;

import com.talktown.common.IpAddressInfo;
import com.talktown.config.RedisConfig;
import com.talktown.dto.EmailDTO;
import com.talktown.entity.User;
import com.talktown.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IpAddressService ipAddressService;

    private final JedisPool jedisPool;
    private final long OTP_EXPIRATION_MINUTES = 3;
    private final long OTP_REQUEST_EXPIRATION_MINUTES = 60; // 1 hour
    private final int MAX_OTP_SEND_COUNT_PER_IP = 5;

    @Autowired
    public OTPService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public String generateOTP() {
        // Tạo mã OTP ngẫu nhiên, ví dụ: 123456
        return String.format("%06d", new Random().nextInt(900000) + 100000);
    }

    public void saveOTP(String email, String otp) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Lưu mã OTP và thời gian hết hạn vào Redis
            jedis.setex(email, (int) TimeUnit.MINUTES.toSeconds(OTP_EXPIRATION_MINUTES), otp);
        }
    }

    public String getOTPFromRedis(String email) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(email);
        }
    }

    public void sendOtp(String email, HttpServletRequest request) {
        // Check if email is already registered
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Get IP address information
        String ipAddress = getClientIpAddress(request);
        IpAddressInfo ipAddressInfo = ipAddressService.getIpAddressInfo(ipAddress);
        if (ipAddressInfo != null && ipAddressInfo.sendCount >= MAX_OTP_SEND_COUNT_PER_IP) {
            throw new IllegalArgumentException("Maximum number of OTP send requests reached for this IP address");
        }

        // Generate and save OTP
        String otp = generateOTP();
        saveOTP(email, otp);

        // Send OTP to email
        emailService.sendOTPEmail(email, otp);

        // Update IP address information
        int newSendCount = ipAddressInfo != null ? ipAddressInfo.sendCount + 1 : 1;
        String lastSendTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ipAddressService.saveIpAddressInfo(ipAddress, newSendCount, lastSendTime);
    }

    public boolean isOTPValid(String email, String otp) {
        // Kiểm tra xem mã OTP có hợp lệ và chưa hết hạn không
        try (Jedis jedis = jedisPool.getResource()) {
            String storedOtp = jedis.get(email);
            return storedOtp != null && storedOtp.equals(otp);
        }
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
