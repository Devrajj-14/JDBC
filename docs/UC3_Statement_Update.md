# [Devraj]: UC3 — Update Salary via Statement

## What was added in this branch

- **Method**: `PayrollDBService.updateSalary(String name, double newSalary)`
- **Approach**: Uses `java.sql.Statement` with string-concatenated SQL
- **SQL**: `UPDATE employee_payroll SET salary = <value> WHERE name = '<name>'`

## Why Statement is used here

UC3 introduces the basic `Statement` approach to demonstrate a direct SQL update.
This sets the stage for UC4, which refactors this to use `PreparedStatement` for:
- SQL injection prevention
- Driver/DB-level query caching

## JUnit Test

```java
// [Devraj]: UC3 — Verify Statement-based update syncs with DB
service.updateSalary("Terisa", 3000000.00); // rows >= 1
service.readByName("Terisa");               // salary == 3000000.00
```

## Expected Output

```
Rows updated: 1
Terisa salary in DB: 3000000.0
```
