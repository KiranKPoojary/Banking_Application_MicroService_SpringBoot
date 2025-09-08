package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupDto {
    private String firstName;
    private String lastName;
    private String password;
    private String username;
    private String email;
    private String phoneNumber;
}
