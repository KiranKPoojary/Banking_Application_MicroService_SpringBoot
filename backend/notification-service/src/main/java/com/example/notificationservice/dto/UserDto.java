package com.example.notificationservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private long user_id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String userstatus;

}
