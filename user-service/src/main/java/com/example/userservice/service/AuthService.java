package com.example.userservice.service;

import com.example.userservice.dto.EmployeeLoginRequest;
import com.example.userservice.dto.UserLoginRequest;
import com.example.userservice.entity.Employee;
import com.example.userservice.entity.User;
import com.example.userservice.repository.EmployeeRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, EmployeeRepository employeeRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticateUser(UserLoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        return jwtUtil.generateToken(claims, user.getUsername());

    }

    public String authenticateEmployee(EmployeeLoginRequest request) {

        Employee emp = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getEmployeeId()));

        // Check username match
        if (!emp.getUsername().equals(request.getUsername())) {
            throw new RuntimeException("Invalid username");
        }

        // Check password match
        if (!passwordEncoder.matches(request.getPassword(), emp.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("empId", emp.getEmployeeId());
        claims.put("username", emp.getUsername());
        claims.put("role", emp.getRole());


        // Generate JWT with empId + username+ role
        return jwtUtil.generateToken(claims,emp.getUsername());


    }
}
