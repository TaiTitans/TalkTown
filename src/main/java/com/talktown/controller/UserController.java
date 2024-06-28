package com.talktown.controller;

import com.talktown.common.StatusResponse;
import com.talktown.dto.UserDTO;
import com.talktown.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<StatusResponse<String>> registerUser(@RequestBody UserDTO userDTO){
        try{
            userService.registerUser(userDTO);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User registered successfully", null));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


}
