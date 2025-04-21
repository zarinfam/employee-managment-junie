-- Drop table if exists
DROP TABLE IF EXISTS employees;

-- Create employees table
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL
);

-- Insert initial data
INSERT INTO employees (first_name, last_name, email, department) VALUES
    ('John', 'Doe', 'john.doe@example.com', 'IT'),
    ('Jane', 'Smith', 'jane.smith@example.com', 'HR'),
    ('Mike', 'Johnson', 'mike.johnson@example.com', 'Finance');
