package com.saeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saeed.model.Employee;
import com.saeed.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
@Import(EmployeeControllerTest.EmployeeServiceTestConfig.class)
public class EmployeeControllerTest {

    @TestConfiguration
    static class EmployeeServiceTestConfig {
        @Bean
        public EmployeeService employeeService() {
            return mock(EmployeeService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllEmployees() throws Exception {
        // Arrange
        when(employeeService.getAllEmployees()).thenReturn(
                Arrays.asList(
                        new Employee(1L, "John", "Doe", "john.doe@example.com", "IT"),
                        new Employee(2L, "Jane", "Smith", "jane.smith@example.com", "HR")
                )
        );

        // Act & Assert
        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    public void testGetEmployeeById_Found() throws Exception {
        // Arrange
        Long employeeId = 1L;
        when(employeeService.getEmployeeById(employeeId)).thenReturn(
                Optional.of(new Employee(employeeId, "John", "Doe", "john.doe@example.com", "IT"))
        );

        // Act & Assert
        mockMvc.perform(get("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        // Arrange
        Long employeeId = 999L;
        when(employeeService.getEmployeeById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateEmployee() throws Exception {
        // Arrange
        Employee newEmployee = new Employee(null, "Alice", "Johnson", "alice.johnson@example.com", "Marketing");
        Employee savedEmployee = new Employee(4L, "Alice", "Johnson", "alice.johnson@example.com", "Marketing");

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(savedEmployee);

        // Act & Assert
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.email").value("alice.johnson@example.com"))
                .andExpect(jsonPath("$.department").value("Marketing"));

        verify(employeeService).createEmployee(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_Success() throws Exception {
        // Arrange
        Long employeeId = 1L;
        Employee employeeToUpdate = new Employee(employeeId, "John", "Smith", "john.smith@example.com", "Sales");

        when(employeeService.updateEmployee(eq(employeeId), any(Employee.class))).thenReturn(employeeToUpdate);

        // Act & Assert
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("john.smith@example.com"))
                .andExpect(jsonPath("$.department").value("Sales"));

        verify(employeeService).updateEmployee(eq(employeeId), any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_NotFound() throws Exception {
        // Arrange
        Long employeeId = 999L;
        Employee employeeToUpdate = new Employee(employeeId, "Unknown", "Person", "unknown@example.com", "Unknown");

        when(employeeService.updateEmployee(eq(employeeId), any(Employee.class)))
                .thenThrow(new RuntimeException("Employee not found with id: " + employeeId));

        // Act & Assert
        mockMvc.perform(put("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeToUpdate)))
                .andExpect(status().isNotFound());

        verify(employeeService).updateEmployee(eq(employeeId), any(Employee.class));
    }

    @Test
    public void testDeleteEmployee_Success() throws Exception {
        // Arrange
        Long employeeId = 1L;
        when(employeeService.deleteEmployee(employeeId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(employeeId);
    }

    @Test
    public void testDeleteEmployee_NotFound() throws Exception {
        // Arrange
        Long employeeId = 999L;
        when(employeeService.deleteEmployee(employeeId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/employees/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService).deleteEmployee(employeeId);
    }
}
