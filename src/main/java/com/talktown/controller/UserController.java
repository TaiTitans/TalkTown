package com.talktown.controller;

import com.talktown.common.LoginRequest;
import com.talktown.common.LoginResponse;
import com.talktown.common.StatusResponse;
import com.talktown.dto.UserDTO;
import com.talktown.service.UserService;
import com.talktown.util.JwtTokenProvider;
import com.talktown.util.TokenExtractor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/register")
    public ResponseEntity<StatusResponse<String>> registerUser(@RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.registerUser(userDTO, otp);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User registered successfully", null));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/email/{id}")
    public ResponseEntity<StatusResponse<UserDTO>> updateEmail(@PathVariable int id, @RequestBody UserDTO userDTO, @RequestParam String otp){
        try{
            userService.updateEmail(userDTO, otp, id);
            return ResponseEntity.ok(new StatusResponse<>("Success", "User updated successfully", userDTO));
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PatchMapping("/user/password")
    public ResponseEntity<StatusResponse<String>> resetPassword(@RequestHeader("Authorization") String authHeader, @RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            String token = TokenExtractor.extractToken(authHeader);
            if (token == null) {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Invalid token format", null));
            }
            if (jwtTokenProvider.validateToken(token)) {
                userService.resetPassword(token, oldPassword, newPassword);
                return ResponseEntity.ok(new StatusResponse<>("Success", "Password updated successfully", null));
            } else {
                return ResponseEntity.badRequest().body(new StatusResponse<>("Error", "Token is not valid", null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponse<>("Error", e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<StatusResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
            userService.login(loginRequest, response);
            return ResponseEntity.ok(new StatusResponse<>("Success", "Login successful", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StatusResponse<>("Error", "An unexpected error occurred", null));
        }
    }

}
