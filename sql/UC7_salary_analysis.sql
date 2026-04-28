SELECT gender,
SUM(salary) AS total_salary,
AVG(salary) AS avg_salary,
MIN(salary) AS min_salary,
MAX(salary) AS max_salary,
COUNT(*) AS employee_count
FROM employee_payroll
GROUP BY gender;
