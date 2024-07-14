package com.talktown.controller;

import com.talktown.common.StatusResponse;
import com.talktown.dto.UserDTO;
import com.talktown.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<StatusResponse<String>> registerUser(@RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.registerUser(userDTO, otp);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User registered successfully", null));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/{id}")
    public ResponseEntity<StatusResponse<UserDTO>> updateEmail(@PathVariable int id, @RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.updateEmail(userDTO, otp, id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User updated successfully", userDTO));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }





}
