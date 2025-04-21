package com.saeed.repository;

import com.saeed.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(JdbcEmployeeRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class JdbcEmployeeRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("employee_db_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private JdbcEmployeeRepository employeeRepository;

    @Autowired
    private JdbcClient jdbcClient;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.sql.init.schema-locations", () -> "classpath:schema.sql");
    }

    @BeforeEach
    void setUp() {
        // Clean up the table and reset the sequence before each test
        jdbcClient.sql("DELETE FROM employees").update();
        jdbcClient.sql("ALTER SEQUENCE employees_id_seq RESTART WITH 1").update();

        // Insert test data
        jdbcClient.sql("INSERT INTO employees (first_name, last_name, email, department) VALUES ('John', 'Doe', 'john.doe@example.com', 'IT')").update();
        jdbcClient.sql("INSERT INTO employees (first_name, last_name, email, department) VALUES ('Jane', 'Smith', 'jane.smith@example.com', 'HR')").update();
        jdbcClient.sql("INSERT INTO employees (first_name, last_name, email, department) VALUES ('Mike', 'Johnson', 'mike.johnson@example.com', 'Finance')").update();
    }

    @Test
    void testFindAll() {
        // Act
        List<Employee> employees = employeeRepository.findAll();

        // Assert
        assertNotNull(employees);
        assertEquals(3, employees.size());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Jane", employees.get(1).getFirstName());
        assertEquals("Mike", employees.get(2).getFirstName());
    }

    @Test
    void testFindById_Found() {
        // Act
        Optional<Employee> employee = employeeRepository.findById(1L);

        // Assert
        assertTrue(employee.isPresent());
        assertEquals("John", employee.get().getFirstName());
        assertEquals("Doe", employee.get().getLastName());
        assertEquals("john.doe@example.com", employee.get().getEmail());
        assertEquals("IT", employee.get().getDepartment());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Employee> employee = employeeRepository.findById(999L);

        // Assert
        assertFalse(employee.isPresent());
    }

    @Test
    void testSave() {
        // Arrange
        Employee newEmployee = new Employee(null, "Alice", "Johnson", "alice.johnson@example.com", "Marketing");

        // Act
        Employee savedEmployee = employeeRepository.save(newEmployee);

        // Assert
        assertNotNull(savedEmployee.getId());
        assertEquals("Alice", savedEmployee.getFirstName());
        assertEquals("Johnson", savedEmployee.getLastName());
        assertEquals("alice.johnson@example.com", savedEmployee.getEmail());
        assertEquals("Marketing", savedEmployee.getDepartment());

        // Verify it's in the database
        Optional<Employee> retrievedEmployee = employeeRepository.findById(savedEmployee.getId());
        assertTrue(retrievedEmployee.isPresent());
        assertEquals("Alice", retrievedEmployee.get().getFirstName());
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        Employee employeeToUpdate = new Employee(1L, "John", "Smith", "john.smith@example.com", "Sales");

        // Act
        Employee updatedEmployee = employeeRepository.update(employeeToUpdate);

        // Assert
        assertEquals(1L, updatedEmployee.getId());
        assertEquals("John", updatedEmployee.getFirstName());
        assertEquals("Smith", updatedEmployee.getLastName());
        assertEquals("john.smith@example.com", updatedEmployee.getEmail());
        assertEquals("Sales", updatedEmployee.getDepartment());

        // Verify it's updated in the database
        Optional<Employee> retrievedEmployee = employeeRepository.findById(1L);
        assertTrue(retrievedEmployee.isPresent());
        assertEquals("Smith", retrievedEmployee.get().getLastName());
        assertEquals("Sales", retrievedEmployee.get().getDepartment());
    }

    @Test
    void testUpdate_NotFound() {
        // Arrange
        Employee nonExistentEmployee = new Employee(999L, "Unknown", "Person", "unknown@example.com", "Unknown");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            employeeRepository.update(nonExistentEmployee);
        });
    }

    @Test
    void testDeleteById_Success() {
        // Act
        boolean result = employeeRepository.deleteById(1L);

        // Assert
        assertTrue(result);

        // Verify it's deleted from the database
        Optional<Employee> retrievedEmployee = employeeRepository.findById(1L);
        assertFalse(retrievedEmployee.isPresent());
    }

    @Test
    void testDeleteById_NotFound() {
        // Act
        boolean result = employeeRepository.deleteById(999L);

        // Assert
        assertFalse(result);
    }
}
