package com.talktown.service;

import com.talktown.dto.UserDTO;
import com.talktown.entity.Role;
import com.talktown.entity.User;
import com.talktown.repository.RoleRepository;
import com.talktown.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;



@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public UserDTO convertUserToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertUserDTOToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }


    public void registerUser(UserDTO userDTO, String otpInput) {
        try {
            // Kiểm tra xem tên đăng nhập đã tồn tại chưa
            User existingUsername = userRepository.findByUsername(userDTO.email);
            if (existingUsername != null) {
                throw new IllegalArgumentException("Username already registered");
            }

            // Kiểm tra xem email đã tồn tại chưa
            User existingUserEmail = userRepository.findByEmail(userDTO.email);
            if (existingUserEmail != null) {
                throw new IllegalArgumentException("Email already registered");
            }

            // Validate OTP
            String otp = otpService.getOTPFromRedis(userDTO.email);
            if (otp == null || !otp.equals(otpInput)) {
                throw new IllegalArgumentException("Invalid OTP");
            }

            // Chuyển đổi UserDTO thành User
            User user = convertUserDTOToUser(userDTO);

            // Tìm vai trò mặc định và gán nó cho người dùng
            Role defaultRole = roleRepository.findByRolename("CUSTOMER");
            user.getRoles().add(defaultRole);

            // Mã hóa mật khẩu và lưu người dùng
            user.setPassword(passwordEncoder.encode(userDTO.password));
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

}
