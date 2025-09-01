package com.example.userservice.controller;

import com.example.userservice.dto.EmployeeLoginRequest;
import com.example.userservice.dto.JwtResponse;
import com.example.userservice.dto.UserLoginRequest;
import com.example.userservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
