-- =============================================================================
-- [Devraj]: Section 1 - UC1 through UC7 SQL Scripts
-- Run these in MySQL Client against the payroll_service database
-- =============================================================================

-- =============================================================================
-- UC1: Create the payroll_service database
-- =============================================================================

-- [Devraj]: UC1 Step 1 - Create the payroll_service database
CREATE DATABASE IF NOT EXISTS payroll_service;

-- [Devraj]: UC1 Step 2 - Verify the database was created
SHOW DATABASES;

-- [Devraj]: UC1 Step 3 - Switch to the payroll_service database
USE payroll_service;


-- =============================================================================
-- UC2: Create the employee_payroll table
-- =============================================================================

-- [Devraj]: UC2 - Create employee_payroll table with auto-increment id and start date
CREATE TABLE IF NOT EXISTS employee_payroll (
    id     INT          AUTO_INCREMENT PRIMARY KEY,  -- [Devraj]: Auto-incremented primary key
    name   VARCHAR(100) NOT NULL,                    -- [Devraj]: Employee full name
    salary DOUBLE       NOT NULL,                    -- [Devraj]: Salary (base pay)
    start  DATE         NOT NULL                     -- [Devraj]: Employment start date
);


-- =============================================================================
-- UC3: Insert employee payroll data
-- =============================================================================

-- [Devraj]: UC3 - Insert sample employee records into employee_payroll
INSERT INTO employee_payroll (name, salary, start) VALUES
    ('Bill',    1000000.00, '2016-01-01'),
    ('Terisa',  2000000.00, '2017-01-01'),
    ('Charlie', 500000.00,  '2020-11-01');


-- =============================================================================
-- UC4: Retrieve all employee payroll data
-- =============================================================================

-- [Devraj]: UC4 - Select all records from employee_payroll
SELECT * FROM employee_payroll;


-- =============================================================================
-- UC5: Retrieve salary for a specific employee + employees joined in a date range
-- =============================================================================

-- [Devraj]: UC5 - View Bill's salary using WHERE condition
SELECT salary FROM employee_payroll WHERE name = 'Bill';

-- [Devraj]: UC5 - View employees who joined between 2018-01-01 and today
--              Uses CAST() and NOW() database functions as specified in the PDF
SELECT * FROM employee_payroll
WHERE start BETWEEN CAST('2018-01-01' AS DATE) AND DATE(NOW());


-- =============================================================================
-- UC6: Add gender column and populate gender values
-- =============================================================================

-- [Devraj]: UC6 Step 1 - Add gender column after the name column using ALTER TABLE
ALTER TABLE employee_payroll
    ADD COLUMN gender CHAR(1) AFTER name;

-- [Devraj]: UC6 Step 2 - Set gender for male employees (Bill, Charlie)
UPDATE employee_payroll
SET gender = 'M'
WHERE name = 'Bill' OR name = 'Charlie';

-- [Devraj]: UC6 Step 3 - Set gender for female employees (Terisa)
UPDATE employee_payroll
SET gender = 'F'
WHERE name = 'Terisa';

-- [Devraj]: UC6 Step 4 - Verify gender values were set correctly
SELECT * FROM employee_payroll;


-- =============================================================================
-- UC7: Aggregate functions grouped by gender (SUM, AVG, MIN, MAX, COUNT)
-- =============================================================================

-- [Devraj]: UC7 - Compute salary aggregates grouped by gender
SELECT
    gender,
    COUNT(*)     AS employee_count,
    SUM(salary)  AS total_salary,
    AVG(salary)  AS average_salary,
    MIN(salary)  AS min_salary,
    MAX(salary)  AS max_salary
FROM employee_payroll
GROUP BY gender;

-- [Devraj]: UC7 - Alternative: SUM only for female employees (as shown in PDF example)
SELECT SUM(salary)
FROM employee_payroll
WHERE gender = 'F'
GROUP BY gender;
