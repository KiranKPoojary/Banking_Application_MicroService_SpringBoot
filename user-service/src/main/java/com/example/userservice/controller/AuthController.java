package com.example.userservice.controller;

import com.example.userservice.dto.EmployeeLoginRequest;
import com.example.userservice.dto.JwtResponse;
import com.example.userservice.dto.UserLoginRequest;
import com.example.userservice.entity.AccessLog;
import com.example.userservice.entity.Employee;
import com.example.userservice.entity.User;
import com.example.userservice.entity.enums.UserAction;
import com.example.userservice.service.AccessLogService;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.EmployeeService;
import com.example.userservice.service.UserService;
import com.example.userservice.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {


    private final AuthService authService;
    private final UserService userService;
    private final AccessLogService accessLogService;
    private final EmployeeService employeeService;

    public AuthController(AuthService authService, UserService userService, AccessLogService accessLogService, EmployeeService employeeService) {
        this.authService = authService;
        this.userService = userService;
        this.accessLogService = accessLogService;
        this.employeeService = employeeService;
    }


    //User Registration
    @PostMapping("/register/user")
    public ResponseEntity<User> loginEmployee(@RequestBody User request,HttpServletRequest httpServletRequest) {
        User user = userService.createUserAll(request);

        AccessLog log = new AccessLog();

        log.setUsername(user.getUsername());
        log.setTimestamp(LocalDateTime.now());
        log.setAction(UserAction.REGISTER);
        log.setUserAgent(RequestUtil.getUserAgent(httpServletRequest));
        log.setIpAddress(RequestUtil.getClientIp(httpServletRequest));

        //Saving access log
        accessLogService.saveAccess(log);
        System.out.println("saved access log register");
        return ResponseEntity.ok(user);
    }
    
    // User login
    @PostMapping("/login/user")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest ) {
        String token = authService.authenticateUser(request);

        AccessLog log = new AccessLog();
//        User user=userService.getUserByUsername(request.getUsername())
//                .orElseThrow(()->new IllegalArgumentException("Username not found"));

        log.setUsername(request.getUsername());
        log.setTimestamp(LocalDateTime.now());
        log.setAction(UserAction.LOGIN);
        log.setUserAgent(RequestUtil.getUserAgent(httpRequest));
        log.setIpAddress(RequestUtil.getClientIp(httpRequest));

        accessLogService.saveAccess(log);
        System.out.println("saved access log login");
        return ResponseEntity.ok(new JwtResponse(token));
    }

    //Employee Register
    @PostMapping("/register/employee")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee request, HttpServletRequest httpRequest ) {
        Employee  employee=employeeService.createEmployee(request);

        AccessLog log = new AccessLog();

        log.setUsername(request.getUsername());
        log.setTimestamp(LocalDateTime.now());
        log.setAction(UserAction.REGISTER);
        log.setUserAgent(RequestUtil.getUserAgent(httpRequest));
        log.setIpAddress(RequestUtil.getClientIp(httpRequest));

        //Saving access log
        accessLogService.saveAccess(log);
        System.out.println("saved access log register");


        return ResponseEntity.ok(employee);

    }
    // Employee login
    @PostMapping("/login/employee")
    public ResponseEntity<?> loginEmployee(@RequestBody EmployeeLoginRequest request, HttpServletRequest httpRequest ) {
        String token = authService.authenticateEmployee(request);

        AccessLog log = new AccessLog();

        log.setUsername(request.getUsername());
        log.setTimestamp(LocalDateTime.now());
        log.setAction(UserAction.LOGIN);
        log.setUserAgent(RequestUtil.getUserAgent(httpRequest));
        log.setIpAddress(RequestUtil.getClientIp(httpRequest));

        accessLogService.saveAccess(log);
        System.out.println("saved access log login");

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
