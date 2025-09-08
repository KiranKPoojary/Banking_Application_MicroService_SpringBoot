package com.example.userservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoginRequest {
    private String username;
    private String password;
}
