package com.example.userservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeLoginRequest {
    private Long employeeId;
    private String username;
    private String password;
}
