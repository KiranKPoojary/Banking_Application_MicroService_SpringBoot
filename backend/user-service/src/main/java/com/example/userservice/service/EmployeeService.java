package com.example.userservice.service;

import com.example.userservice.entity.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee createEmployee(@RequestBody Employee employee);
    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(@PathVariable Long id);

    ResponseEntity<Employee> updateEmployee(@PathVariable Long id,@RequestBody Employee empDetails);

    ResponseEntity<Employee> changePassword(Long employeeId, String oldPassword, String newPassword);

    ResponseEntity<String> deleteEmployee(Long id);
}
