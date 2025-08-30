package com.example.userservice.controller;


import com.example.userservice.dto.AccountDto;
import com.example.userservice.dto.LoginRequestDto;
import com.example.userservice.dto.TransactionDto;
import com.example.userservice.dto.UserSignupDto;
import com.example.userservice.entity.User;
import com.example.userservice.entity.enums.UserStatus;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ✅ Create User with less data
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserSignupDto user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isAuthenticated = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        if (isAuthenticated) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUserAll(user));
    }

    // ✅ Get All Users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //get all accounts of user from account service
    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<AccountDto>> getUserAccounts(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserAccounts(id));
    }

    //get account transactions from account service
    @GetMapping("/{id}/{accountId}/transactions")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@PathVariable Long id,@PathVariable Long accountId) {
        return ResponseEntity.ok(userService.getUserTransactions(id,accountId));
    }

    // ✅ Update User
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    // ✅ Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Activate User
    @PatchMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateStatus(id, UserStatus.ACTIVE));
    }

    // ✅ Deactivate User
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateStatus(id, UserStatus.INACTIVE));
    }

    // ✅ Check if user is active
    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> isUserActive(@PathVariable Long id) {
        return ResponseEntity.ok(userService.isActive(id));
    }
}
