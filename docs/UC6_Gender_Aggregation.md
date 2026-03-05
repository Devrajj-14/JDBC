# [Devraj]: UC6 — Gender Aggregation Statistics

## What was added in this branch

- **Model**: `GenderStats` DTO (gender, empCount, totalSalary, avgSalary, minSalary, maxSalary)
- **Method**: `PayrollDBService.getGenderStats()`
- **SQL**: `SELECT gender, COUNT(*), SUM(salary), AVG(salary), MIN(salary), MAX(salary) FROM employee_payroll GROUP BY gender`
- Returns a `List<GenderStats>` (not raw ResultSet) to ensure proper resource management

## Why DTO Instead of ResultSet

Returning a raw `ResultSet` leaks connection resources. `GenderStats` DTO maps the result in try-with-resources, properly closing connection/statement/resultset.

## JUnit Test

```java
// [Devraj]: UC6 — Verify aggregates are non-negative for each gender
List<GenderStats> stats = service.getGenderStats();
stats.forEach(s -> assertTrue(s.totalSalary >= 0));
```
