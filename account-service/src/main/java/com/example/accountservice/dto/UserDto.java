package com.example.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long user_id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String userstatus;
}
