package com.saeed.repository;

import com.saeed.model.Employee;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEmployeeRepository implements EmployeeRepository {

    private final JdbcClient jdbcClient;

    // SQL statements
    private static final String SQL_FIND_ALL = "SELECT id, first_name, last_name, email, department FROM employees";
    private static final String SQL_FIND_BY_ID = "SELECT id, first_name, last_name, email, department FROM employees WHERE id = ?";
    private static final String SQL_INSERT = "INSERT INTO employees (first_name, last_name, email, department) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, department = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM employees WHERE id = ?";

    public JdbcEmployeeRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Employee> findAll() {
        return jdbcClient.sql(SQL_FIND_ALL)
                .query((rs, rowNum) -> new Employee(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("department")
                ))
                .list();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return jdbcClient.sql(SQL_FIND_BY_ID)
                .param(id)
                .query((rs, rowNum) -> new Employee(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("department")
                ))
                .optional();
    }

    @Override
    @Transactional
    public Employee save(Employee employee) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(SQL_INSERT)
                .param(employee.getFirstName())
                .param(employee.getLastName())
                .param(employee.getEmail())
                .param(employee.getDepartment())
                .update(keyHolder);

        // Extract the ID from the key map
        Number key = (Number) keyHolder.getKeys().get("id");
        Long generatedId = key.longValue();
        employee.setId(generatedId);

        return employee;
    }

    @Override
    @Transactional
    public Employee update(Employee employee) {
        int updated = jdbcClient.sql(SQL_UPDATE)
                .param(employee.getFirstName())
                .param(employee.getLastName())
                .param(employee.getEmail())
                .param(employee.getDepartment())
                .param(employee.getId())
                .update();

        if (updated == 0) {
            throw new RuntimeException("Employee not found with id: " + employee.getId());
        }

        return employee;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        int deleted = jdbcClient.sql(SQL_DELETE)
                .param(id)
                .update();

        return deleted > 0;
    }
}
