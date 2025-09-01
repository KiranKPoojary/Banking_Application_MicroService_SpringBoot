package com.example.userservice.service;

import com.example.userservice.entity.CustomUserDetails;
import com.example.userservice.entity.Employee;
import com.example.userservice.entity.User;
import com.example.userservice.repository.EmployeeRepository;
import com.example.userservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First check in users table
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return new CustomUserDetails(user.getUsername(), user.getPassword(),user.getRole().toString());
        }

        // If not found, check in employees table
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CustomUserDetails(employee.getUsername(), employee.getPassword(), employee.getRole().toString());
    }
}
