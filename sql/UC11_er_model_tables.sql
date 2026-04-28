CREATE TABLE employee(
emp_id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(150),
gender CHAR(1),
phone VARCHAR(20),
address VARCHAR(255)
);

CREATE TABLE department(
dept_id INT AUTO_INCREMENT PRIMARY KEY,
dept_name VARCHAR(150)
);

CREATE TABLE payroll(
emp_id INT,
basic_pay DOUBLE,
deductions DOUBLE,
taxable_pay DOUBLE,
tax DOUBLE,
net_pay DOUBLE,
FOREIGN KEY(emp_id) REFERENCES employee(emp_id)
);

CREATE TABLE employee_department(
emp_id INT,
dept_id INT,
PRIMARY KEY(emp_id,dept_id),
FOREIGN KEY(emp_id) REFERENCES employee(emp_id),
FOREIGN KEY(dept_id) REFERENCES department(dept_id)
);
