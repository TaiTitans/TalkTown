package com.talktown.service;

import com.talktown.config.SecurityConfig;
import com.talktown.dto.UserDTO;
import com.talktown.dto.UserProfileDTO;
import com.talktown.entity.User;
import com.talktown.entity.UserProfile;
import com.talktown.repository.UserProfileRepository;
import com.talktown.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    private static final Logger log =  LoggerFactory.getLogger(UserProfileService.class);
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserProfileDTO convertUserProfileToUserProfileDTO(UserProfile userProfile) {
        return modelMapper.map(userProfile, UserProfileDTO.class);
    }

    public UserProfile convertUserProfileDTOToUserProfile(UserProfileDTO userProfileDTO) {
        return modelMapper.map(userProfileDTO, UserProfile.class);
    }


    public void addProfile(UserProfileDTO userProfileDTO) {
        try {
            log.debug("Attempting to add profile for user ID: {}", userProfileDTO.user_id);
            Optional<User> optionalUser = userRepository.findById(userProfileDTO.user_id);
            if(optionalUser.isPresent()) {
                log.debug("User found, converting DTO to UserProfile");
                UserProfile userProfile = convertUserProfileDTOToUserProfile(userProfileDTO);
                log.debug("Saving UserProfile to database");
                userProfileRepository.save(userProfile);
                log.debug("UserProfile saved successfully");
            } else {
                log.error("User not found for ID: {}", userProfileDTO.user_id);
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            log.error("Error adding user profile", e);
            throw new IllegalArgumentException("Invalid user profile", e);
        }
    }
}
