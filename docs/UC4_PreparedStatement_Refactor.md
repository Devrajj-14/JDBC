# [Devraj]: UC4 — Refactor to PreparedStatement + Singleton

## What was added in this branch

1. **Method**: `PayrollDBService.updateSalaryPrepared(String name, double newSalary)`
   - Uses `PreparedStatement` with parameterised `?` placeholders
   - SQL injection safe + DB/driver-level query caching

2. **Method**: `PayrollDBService.readByName(String name)`
   - Retrieves employee by name using `PreparedStatement`
   - Reuses `buildFromResultSet()` helper for DRY code

3. **Singleton Refactor**: `PayrollDBService.getInstance()`
   - Service is now a singleton so `PreparedStatement` caches survive across calls

## Why PreparedStatement is Better

| Feature | Statement (UC3) | PreparedStatement (UC4) |
|---------|----------------|------------------------|
| SQL Injection | ❌ Vulnerable | ✅ Safe |
| Query Caching | ❌ No | ✅ Driver + DB level |
| Reusability | ❌ Rebuild each time | ✅ Reuse with new params |

## JUnit Test

```java
// [Devraj]: UC4 — Compare object with DB after PreparedStatement update
service.updateSalaryPrepared("Terisa", 3000000.00); // rows >= 1
service.readByName("Terisa");                        // salary == 3000000.00
```
