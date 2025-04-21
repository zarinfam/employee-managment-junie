package com.saeed.repository;

import com.saeed.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity
 */
public interface EmployeeRepository {

    /**
     * Find all employees
     * @return list of all employees
     */
    List<Employee> findAll();

    /**
     * Find employee by id
     * @param id employee id
     * @return optional containing employee if found
     */
    Optional<Employee> findById(Long id);

    /**
     * Save a new employee
     * @param employee employee to save
     * @return saved employee with generated id
     */
    Employee save(Employee employee);

    /**
     * Update an existing employee
     * @param employee employee to update
     * @return updated employee
     */
    Employee update(Employee employee);

    /**
     * Delete an employee by id
     * @param id employee id to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteById(Long id);
}
