package com.example.userservice.service.implement;

import com.example.userservice.client.AccountClient;
import com.example.userservice.dto.AccountDto;
import com.example.userservice.dto.TransactionDto;
import com.example.userservice.dto.UserRegisteredEvent;
import com.example.userservice.dto.UserSignupDto;
import com.example.userservice.entity.AccessLog;
import com.example.userservice.entity.User;
import com.example.userservice.entity.enums.Role;
import com.example.userservice.entity.enums.UserAction;
import com.example.userservice.entity.enums.UserStatus;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.AccessLogRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import com.example.userservice.service.kafka.KafkaUserProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

   final UserRepository userRepository;
   final AccessLogRepository accessLogRepository;
   final PasswordEncoder passwordEncoder;
   final AccountClient accountClient;
  private final KafkaUserProducer userEventProducer;
   final ObjectMapper objectMapper;

   public UserServiceImpl(UserRepository userRepository, AccessLogRepository accessLogRepository, PasswordEncoder passwordEncoder, AccountClient accountClient, KafkaUserProducer userEventProducer, ObjectMapper objectMapper) {
       this.userRepository = userRepository;
       this.accessLogRepository = accessLogRepository;
       this.passwordEncoder = passwordEncoder;
       this.accountClient=accountClient;
       this.userEventProducer = userEventProducer;
       this.objectMapper = objectMapper;
   }

   @Override
   public User createUserAll(User user) {
       user.setStatus(UserStatus.ACTIVE);
       user.setPassword(passwordEncoder.encode(user.getPassword()));
       return userRepository.save(user);
   }

    @Override
    public User createUser(UserSignupDto user) {
        // Validate first name
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }

        // Validate last name
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (user.getUsername().length() < 4 || user.getUsername().length() > 20) {
            throw new IllegalArgumentException("Username must be between 4 and 20 characters.");
        }

        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        if (!user.getPassword().matches(".*[A-Z].*") ||
                !user.getPassword().matches(".*[a-z].*") ||
                !user.getPassword().matches(".*\\d.*") ||
                !user.getPassword().matches(".*[!@#$%^&*()].*")) {
            throw new IllegalArgumentException("Password must contain uppercase, lowercase, digit, and special character.");
        }

        // Validate email
        if (user.getEmail() == null || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // Validate phone number
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Phone number must be 10 digits.");
        }

        // If all validations pass, create and return the User object
        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(user.getPassword()); // Consider hashing before saving
        newUser.setEmail(user.getEmail());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setRole(Role.CUSTOMER);
        newUser.setPassword(passwordEncoder.encode(user.getPassword())); // Secure password storage
        User savedUser = userRepository.save(newUser);
        //Sending User Registered Producer Payload
        try {
            UserRegisteredEvent event=new UserRegisteredEvent();
            event.setUserId(newUser.getId());
            event.setEmail(newUser.getEmail());
            event.setFirstName(newUser.getFirstName());
            event.setLastName(newUser.getLastName());
            event.setUsername(newUser.getUsername());


            // Send event to Kafka
            userEventProducer.sendUserRegisteredEvent(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send user registered event", e);
        }

        return savedUser;
    }

    @Override
    public boolean authenticate(String username, String password) {
        if(userRepository.findByUsername(username).isPresent()){
            if(verifyPassword(username,password)){
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found with id: " + id));
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //getting all user accounts
    @Override
    public List<AccountDto> getUserAccounts(Long userId) {

       return accountClient.getAccountsByUserId(userId);
    }


    //Getting transactions for a specific account
    @Override
    public List<TransactionDto> getUserTransactions(Long userId, Long accountId) {
       return accountClient.getAccountTransactions(accountId);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setUsername(userDetails.getUsername());
            user.setStatus(userDetails.getStatus());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public void deleteUser(Long id) {

       userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {

       return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {

       return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {

       return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {

       return userRepository.existsByEmail(email);
    }

    @Override
    public boolean verifyPassword(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        userRepository.findById(userId).ifPresent(user -> {
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Old password is incorrect");
            }
        });
    }

    @Override
    public User updateStatus(Long id,UserStatus status) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(status);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public boolean isActive(Long id) {
        return userRepository.findById(id).map(user-> user.getStatus()==UserStatus.ACTIVE).orElse(false);
    }
//
//    @Override
//    public void assignRole(Long userId, String role) {
//        userRepository.findById(userId).ifPresent(user -> {
//            user.getRole().;
//            userRepository.save(user);
//        });
//    }
//
//    @Override
//    public void removeRole(Long userId, String role) {
//        userRepository.findById(userId).ifPresent(user -> {
//            user.getRole().remove(role);
//            userRepository.save(user);
//        });
//    }

    @Override
    public void logUserAction(Long userId, UserAction action, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setAction(action);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setTimestamp(LocalDateTime.now());

        accessLogRepository.save(log);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
