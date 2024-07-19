package com.talktown.controller;

import com.talktown.common.StatusResponse;
import com.talktown.dto.UserProfileDTO;
import com.talktown.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/profile")
    public ResponseEntity<StatusResponse<UserProfileDTO>> addUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        try{
            userProfileService.addProfile(userProfileDTO);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Add profile successfully", userProfileDTO));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "Add profile failed", null));
        }
    }


}
