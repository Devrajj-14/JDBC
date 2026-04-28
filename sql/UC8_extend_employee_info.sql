ALTER TABLE employee_payroll
ADD phone_number VARCHAR(20),
ADD address VARCHAR(255) DEFAULT 'TBD',
ADD department VARCHAR(150) NOT NULL;
