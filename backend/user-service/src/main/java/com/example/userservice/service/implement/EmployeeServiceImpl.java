package com.example.userservice.service.implement;

import com.example.userservice.entity.Employee;
import com.example.userservice.repository.EmployeeRepository;
import com.example.userservice.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);

    }

    @Override
    public List<Employee> getAllEmployees()
    {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return  employeeRepository.findById(id);
    }

    @Override
    public ResponseEntity<Employee> updateEmployee(Long id, Employee empDetails) {
        return employeeRepository.findById(id).map(emp -> {
            emp.setFirstName(empDetails.getFirstName());
            emp.setLastName(empDetails.getLastName());
            emp.setEmail(empDetails.getEmail());
            emp.setRole(empDetails.getRole());
            emp.setBranch(empDetails.getBranch());
            emp.setStatus(empDetails.getStatus());
            return ResponseEntity.ok(employeeRepository.save(emp));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Employee> changePassword(Long employeeId, String oldPassword, String newPassword) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        Employee updatedEmployee = employeeRepository.save(employee);

        return ResponseEntity.ok(updatedEmployee);
    }

    @Override
    public ResponseEntity<String> deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Employee with ID " + id + " not found");
        }

        employeeRepository.deleteById(id);
        return ResponseEntity.ok("Employee with Id"+ id+" deleted successfully");
    }
}
