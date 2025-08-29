package com.example.userservice.controller;

import com.example.userservice.entity.Employee;
import com.example.userservice.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create Employee
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    // Get All Employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get Employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update Employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee empDetails) {
        return employeeRepository.findById(id).map(emp -> {
            emp.setFirstName(empDetails.getFirstName());
            emp.setLastName(empDetails.getLastName());
            emp.setEmail(empDetails.getEmail());
            emp.setPasswordHash(empDetails.getPasswordHash());
            emp.setRole(empDetails.getRole());
            emp.setBranch(empDetails.getBranch());
            emp.setStatus(empDetails.getStatus());
            return ResponseEntity.ok(employeeRepository.save(emp));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete Employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        return employeeRepository.findById(id).map(emp -> {
            employeeRepository.delete(emp);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }
}

