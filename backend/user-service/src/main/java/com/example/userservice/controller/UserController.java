package com.example.userservice.controller;


import com.example.userservice.dto.AccountDto;
import com.example.userservice.dto.TransactionDto;
import com.example.userservice.entity.CustomUserDetails;
import com.example.userservice.entity.User;
import com.example.userservice.entity.enums.UserStatus;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0/users")
@RequiredArgsConstructor
@Tag(name = "USER CONTROLLER",description = "Operations related to users")
public class UserController {

    private final UserService userService;


    // ✅ Get All Users
    @PreAuthorize("hasAnyRole('ADMIN', 'EXECUTIVE', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        System.out.println(authentication.getAuthorities());
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Long getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long Id=userDetails.getId();
        System.out.println(username);
        System.out.println(Id);
        System.out.println(authentication.getAuthorities());
        return Id;
    }
//,
    // ✅ Get User by ID
    @PreAuthorize("hasRole('SERVICE') || #id == authentication.principal.id || hasAnyRole('ADMIN', 'MANAGER', 'EXECUTIVE') ")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id,Authentication authentication){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //get all accounts of user from account service
    @PreAuthorize("#id == authentication.principal.id || hasAnyRole('ADMIN', 'MANAGER', 'EXECUTIVE')")
    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<AccountDto>> getUserAccounts(@PathVariable Long id,Authentication authentication) {
        return ResponseEntity.ok(userService.getUserAccounts(id));
    }

    //get account transactions from account service
    @PreAuthorize("#id == authentication.principal.id ||  hasAnyRole('ADMIN', 'MANAGER', 'EXECUTIVE')")
    @GetMapping("/{id}/{accountId}/transactions")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@PathVariable Long id,@PathVariable Long accountId,Authentication authentication) {
        return ResponseEntity.ok(userService.getUserTransactions(id,accountId));
    }

    // ✅ Update User
    @PreAuthorize("#id == authentication.principal.id ||  hasAnyRole('ADMIN', 'MANAGER','EXECUTIVE')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails,Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    // ✅ Delete User
    @PreAuthorize("#id == authentication.principal.id ||  hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,Authentication authentication) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Activate User

    @PatchMapping("/{id}/active")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateStatus(id, UserStatus.ACTIVE));
    }

    // ✅ Deactivate User
    @PatchMapping("/{id}/inactive")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.updateStatus(id, UserStatus.INACTIVE));
    }

    // ✅ Check if user is active
    @GetMapping("/{id}/status")
    public ResponseEntity<Boolean> isUserActive(@PathVariable Long id) {
        return ResponseEntity.ok(userService.isActive(id));
    }
}
