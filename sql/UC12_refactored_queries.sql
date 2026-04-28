SELECT e.name,p.net_pay,d.dept_name
FROM employee e
JOIN payroll p ON e.emp_id=p.emp_id
JOIN employee_department ed ON e.emp_id=ed.emp_id
JOIN department d ON ed.dept_id=d.dept_id;
