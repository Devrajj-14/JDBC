# [Devraj]: UC5 — Date Range Query

## What was added in this branch

- **Method**: `PayrollDBService.readJoinedBetween(LocalDate from, LocalDate to)`
- **SQL**: `SELECT ... WHERE start BETWEEN ? AND ?`
- Converts `LocalDate` → `java.sql.Date` for JDBC compatibility

## JUnit Test

```java
// [Devraj]: UC5 — Employees joined between 2018-01-01 and today
service.readJoinedBetween(LocalDate.of(2018,1,1), LocalDate.now());
// Verify each result's start date falls within range
```
