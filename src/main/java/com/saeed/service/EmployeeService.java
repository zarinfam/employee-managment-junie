package com.saeed.service;

import com.saeed.model.Employee;
import com.saeed.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        // Ensure ID is null for new employees
        employee.setId(null);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee employee) {
        // Ensure the ID in the path matches the ID in the employee object
        employee.setId(id);

        // Check if employee exists
        if (!employeeRepository.findById(id).isPresent()) {
            throw new RuntimeException("Employee not found with id: " + id);
        }

        return employeeRepository.update(employee);
    }

    @Transactional
    public boolean deleteEmployee(Long id) {
        return employeeRepository.deleteById(id);
    }
}
