package com.example.userservice.service;

import com.example.userservice.dto.AccountDto;
import com.example.userservice.dto.TransactionDto;
import com.example.userservice.dto.UserSignupDto;
import com.example.userservice.entity.User;
import com.example.userservice.entity.enums.UserAction;
import com.example.userservice.entity.enums.UserStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // Core CRUD
    User createUser(UserSignupDto user);
    User createUserAll(User user);
    boolean authenticate(String username, String password);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);

    //get accounts of user
    List<AccountDto> getUserAccounts(Long userId);

    //get transactions of user accounts
    List<TransactionDto> getUserTransactions(Long userId, Long accountId);

    // Real-time features
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Authentication / Password mgmt
    boolean verifyPassword(String username, String rawPassword);
    void changePassword(Long userId, String oldPassword, String newPassword);

    // Status Management

    User updateStatus(Long id, UserStatus status);
//    User activateUser(Long id);
//    User deactivateUser(Long id);
////    User suspendUser(Long id);
    boolean isActive(Long id);

//    // Role / Permission
//    void assignRole(Long userId, String role);
//    void removeRole(Long userId, String role);

    // Audit / Logs
    void logUserAction(Long userId, UserAction action, HttpServletRequest request);

    //filter
//    List<User> searchUsers(String keyword); // e.g., by name, email, username

}
