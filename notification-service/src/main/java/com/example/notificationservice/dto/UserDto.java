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
    private String email;
    private String firstName;
    private String lastName;

}
