package com.saeed.service;

import com.saeed.model.Employee;
import com.saeed.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    public void testGetAllEmployees() {
        // Arrange
        List<Employee> mockEmployees = Arrays.asList(
            new Employee(1L, "John", "Doe", "john.doe@example.com", "IT"),
            new Employee(2L, "Jane", "Smith", "jane.smith@example.com", "HR"),
            new Employee(3L, "Mike", "Johnson", "mike.johnson@example.com", "Finance")
        );
        when(employeeRepository.findAll()).thenReturn(mockEmployees);

        // Act
        List<Employee> employees = employeeService.getAllEmployees();

        // Assert
        assertNotNull(employees);
        assertEquals(3, employees.size());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Jane", employees.get(1).getFirstName());
        assertEquals("Mike", employees.get(2).getFirstName());

        verify(employeeRepository).findAll();
    }

    @Test
    public void testGetEmployeeById_Found() {
        // Arrange
        Employee mockEmployee = new Employee(1L, "John", "Doe", "john.doe@example.com", "IT");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        // Act
        Optional<Employee> employee = employeeService.getEmployeeById(1L);

        // Assert
        assertTrue(employee.isPresent());
        assertEquals("John", employee.get().getFirstName());
        assertEquals("Doe", employee.get().getLastName());
        assertEquals("IT", employee.get().getDepartment());

        verify(employeeRepository).findById(1L);
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Employee> employee = employeeService.getEmployeeById(999L);

        // Assert
        assertFalse(employee.isPresent());

        verify(employeeRepository).findById(999L);
    }

    @Test
    public void testCreateEmployee() {
        // Arrange
        Employee newEmployee = new Employee(null, "Alice", "Johnson", "alice.johnson@example.com", "Marketing");
        Employee savedEmployee = new Employee(4L, "Alice", "Johnson", "alice.johnson@example.com", "Marketing");

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        Employee result = employeeService.createEmployee(newEmployee);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        assertEquals("alice.johnson@example.com", result.getEmail());
        assertEquals("Marketing", result.getDepartment());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_Success() {
        // Arrange
        Long employeeId = 1L;
        Employee existingEmployee = new Employee(employeeId, "John", "Doe", "john.doe@example.com", "IT");
        Employee updatedEmployee = new Employee(employeeId, "John", "Smith", "john.smith@example.com", "Sales");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.update(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(employeeId, updatedEmployee);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("john.smith@example.com", result.getEmail());
        assertEquals("Sales", result.getDepartment());

        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).update(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_NotFound() {
        // Arrange
        Long employeeId = 999L;
        Employee updatedEmployee = new Employee(employeeId, "Unknown", "Person", "unknown@example.com", "Unknown");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            employeeService.updateEmployee(employeeId, updatedEmployee);
        });

        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).update(any(Employee.class));
    }

    @Test
    public void testDeleteEmployee_Success() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.deleteById(employeeId)).thenReturn(true);

        // Act
        boolean result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertTrue(result);
        verify(employeeRepository).deleteById(employeeId);
    }

    @Test
    public void testDeleteEmployee_NotFound() {
        // Arrange
        Long employeeId = 999L;
        when(employeeRepository.deleteById(employeeId)).thenReturn(false);

        // Act
        boolean result = employeeService.deleteEmployee(employeeId);

        // Assert
        assertFalse(result);
        verify(employeeRepository).deleteById(employeeId);
    }
}
