-- =============================================================================
-- [Devraj]: Section 2 - UC8 through UC12 SQL Scripts (ER Diagram / Normalization)
-- Run these in MySQL Client after running section1_uc1_to_uc7.sql
-- =============================================================================

USE payroll_service;


-- =============================================================================
-- UC8: Extend employee_payroll to store phone, address, and department
--      department is non-nullable; address has a default value
-- =============================================================================

-- [Devraj]: UC8 - Add phone (multi-valued in ER model, stored flat for now)
ALTER TABLE employee_payroll
    ADD COLUMN phone   VARCHAR(20)  AFTER gender;

-- [Devraj]: UC8 - Add address with DEFAULT 'Unknown' (composite attribute in ER model)
ALTER TABLE employee_payroll
    ADD COLUMN address VARCHAR(255) NOT NULL DEFAULT 'Unknown' AFTER phone;

-- [Devraj]: UC8 - Add department as non-nullable field (enforced at DB level)
ALTER TABLE employee_payroll
    ADD COLUMN department VARCHAR(100) NOT NULL DEFAULT 'Unassigned' AFTER address;

-- [Devraj]: UC8 - Verify the updated table structure
DESCRIBE employee_payroll;


-- =============================================================================
-- UC9: Add payroll break-down columns
--      Basic Pay, Deductions, Taxable Pay, Income Tax, Net Pay
-- =============================================================================

-- [Devraj]: UC9 - Rename existing salary column to basic_pay for clarity
ALTER TABLE employee_payroll
    CHANGE COLUMN salary basic_pay DOUBLE NOT NULL;

-- [Devraj]: UC9 - Add payroll computation columns
ALTER TABLE employee_payroll
    ADD COLUMN deductions   DOUBLE NOT NULL DEFAULT 0.0  AFTER basic_pay,
    ADD COLUMN taxable_pay  DOUBLE NOT NULL DEFAULT 0.0  AFTER deductions,
    ADD COLUMN income_tax   DOUBLE NOT NULL DEFAULT 0.0  AFTER taxable_pay,
    ADD COLUMN net_pay      DOUBLE NOT NULL DEFAULT 0.0  AFTER income_tax;

-- [Devraj]: UC9 - Verify extended payroll columns
DESCRIBE employee_payroll;


-- =============================================================================
-- UC10: Make Terisa part of Sales and Marketing Department
--       Shows that multi-department support creates redundancy in flat table
--       → motivates normalization into separate Entity tables
-- =============================================================================

-- [Devraj]: UC10 - Insert Terisa again as part of Sales and Marketing
--           (This creates a second Terisa row, demonstrating redundancy problem)
INSERT INTO employee_payroll (name, gender, phone, address, department, basic_pay, deductions, taxable_pay, income_tax, net_pay, start)
VALUES ('Terisa', 'F', '9876543210', '123 Main St', 'Sales And Marketing', 3000000.00, 0.0, 3000000.00, 300000.00, 2700000.00, '2017-01-01');

-- [Devraj]: UC10 - Notice 2 Terisa rows — demonstrates the redundancy issue
SELECT * FROM employee_payroll WHERE name = 'Terisa';

-- =============================================================================
-- UC10 / ER Diagram — Normalize into 3 entities: Employee, Department, EmployeeDepartment
-- =============================================================================

-- [Devraj]: Create Employee table (normalized entity)
CREATE TABLE IF NOT EXISTS employee (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    gender     CHAR(1),
    phone      VARCHAR(20),
    address    VARCHAR(255) NOT NULL DEFAULT 'Unknown',
    start      DATE         NOT NULL
);

-- [Devraj]: Create Department table (normalized entity)
CREATE TABLE IF NOT EXISTS department (
    id   INT          AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE   -- [Devraj]: Department name must be unique
);

-- [Devraj]: Create employee_department join table (Many-to-Many: one employee can be in many depts)
CREATE TABLE IF NOT EXISTS employee_department (
    employee_id   INT NOT NULL,
    department_id INT NOT NULL,
    PRIMARY KEY (employee_id, department_id),
    FOREIGN KEY (employee_id)   REFERENCES employee(id)   ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE CASCADE
);

-- [Devraj]: Create payroll table (One-to-One with employee, avoids redundancy)
CREATE TABLE IF NOT EXISTS payroll (
    id           INT    AUTO_INCREMENT PRIMARY KEY,
    employee_id  INT    NOT NULL UNIQUE,               -- [Devraj]: One payroll record per employee
    basic_pay    DOUBLE NOT NULL,
    deductions   DOUBLE NOT NULL DEFAULT 0.0,
    taxable_pay  DOUBLE NOT NULL DEFAULT 0.0,
    income_tax   DOUBLE NOT NULL DEFAULT 0.0,
    net_pay      DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
);


-- =============================================================================
-- UC11: Populate new normalized tables
-- =============================================================================

-- [Devraj]: UC11 - Insert employees into normalized employee table
INSERT INTO employee (name, gender, phone, address, start) VALUES
    ('Bill',    'M', '1111111111', '10 Baker St',     '2016-01-01'),
    ('Terisa',  'F', '9876543210', '123 Main St',     '2017-01-01'),
    ('Charlie', 'M', '2222222222', '5 Park Lane',     '2020-11-01');

-- [Devraj]: UC11 - Insert departments
INSERT INTO department (name) VALUES
    ('Engineering'),
    ('Sales And Marketing');

-- [Devraj]: UC11 - Assign departments (Bill=Engineering, Charlie=Engineering, Terisa=BOTH)
INSERT INTO employee_department (employee_id, department_id) VALUES
    (1, 1),   -- [Devraj]: Bill → Engineering
    (2, 1),   -- [Devraj]: Terisa → Engineering
    (2, 2),   -- [Devraj]: Terisa → Sales And Marketing (Many-to-Many)
    (3, 1);   -- [Devraj]: Charlie → Engineering

-- [Devraj]: UC11 - Insert payroll records (One-to-One with employee)
INSERT INTO payroll (employee_id, basic_pay, deductions, taxable_pay, income_tax, net_pay) VALUES
    (1, 1000000.00, 0.0, 1000000.00, 100000.00, 900000.00),  -- [Devraj]: Bill
    (2, 3000000.00, 0.0, 3000000.00, 300000.00, 2700000.00), -- [Devraj]: Terisa
    (3,  500000.00, 0.0,  500000.00,  50000.00,  450000.00); -- [Devraj]: Charlie


-- =============================================================================
-- UC12: Redo UC4 / UC5 / UC7 queries on the new normalized table structure
-- =============================================================================

-- [Devraj]: UC12 / UC4 redo - Retrieve ALL employee payroll data from normalized tables
SELECT e.id, e.name, e.gender, e.start,
       p.basic_pay, p.deductions, p.taxable_pay, p.income_tax, p.net_pay
FROM employee e
JOIN payroll p ON p.employee_id = e.id
ORDER BY e.id;

-- [Devraj]: UC12 / UC5 redo - Employees who joined between specific dates
SELECT e.id, e.name, e.start, p.basic_pay
FROM employee e
JOIN payroll p ON p.employee_id = e.id
WHERE e.start BETWEEN CAST('2016-01-01' AS DATE) AND DATE(NOW());

-- [Devraj]: UC12 / UC7 redo - Aggregate salary stats by gender using normalized tables
SELECT
    e.gender,
    COUNT(*)          AS employee_count,
    SUM(p.basic_pay)  AS total_basic_pay,
    AVG(p.basic_pay)  AS avg_basic_pay,
    MIN(p.basic_pay)  AS min_basic_pay,
    MAX(p.basic_pay)  AS max_basic_pay
FROM employee e
JOIN payroll p ON p.employee_id = e.id
GROUP BY e.gender;

-- [Devraj]: UC12 / UC10 - Show Terisa's departments using the M:M join table
SELECT e.name, d.name AS department
FROM employee e
JOIN employee_department ed ON ed.employee_id   = e.id
JOIN department d           ON d.id             = ed.department_id
WHERE e.name = 'Terisa';
