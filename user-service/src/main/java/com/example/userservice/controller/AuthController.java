package com.example.userservice.controller;

import com.example.userservice.dto.EmployeeLoginRequest;
import com.example.userservice.dto.JwtResponse;
import com.example.userservice.dto.UserLoginRequest;
import com.example.userservice.entity.User;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {


    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }


    //User Registration
    @PostMapping("/register/user")
    public ResponseEntity<User> loginEmployee(@RequestBody User request) {
        User user = userService.createUserAll(request);
        return ResponseEntity.ok(user);
    }
    
    // User login
    @PostMapping("/login/user")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        String token = authService.authenticateUser(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    // Employee login
    @PostMapping("/login/employee")
    public ResponseEntity<?> loginEmployee(@RequestBody EmployeeLoginRequest request) {
        String token = authService.authenticateEmployee(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
