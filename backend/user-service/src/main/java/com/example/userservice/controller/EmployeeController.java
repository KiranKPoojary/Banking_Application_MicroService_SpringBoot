package com.example.userservice.controller;

import com.example.userservice.dto.ChangePassword;
import com.example.userservice.entity.Employee;
import com.example.userservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // Admin: Create Employee
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }


    //Admin: Get all employees
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get Employee by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update Employee
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee empDetails) {
      return employeeService.updateEmployee(id,empDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EXECUTIVE')")
    @PostMapping("/{id}/changePassword")
    public ResponseEntity<Employee> changePassword(@PathVariable Long id, @RequestBody ChangePassword changePassword) {
        return employeeService.changePassword(id,changePassword.getOldPassword(), changePassword.getNewPassword());
    }


    // Delete Employee
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }
}

