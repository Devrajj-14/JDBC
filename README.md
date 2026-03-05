# JDBC Employee Payroll Service

A Java Maven project demonstrating JDBC connectivity and CRUD operations on a MySQL `payroll_service` database.

## Project Structure

```
jdbc-employee-payroll/
├── pom.xml
├── sql/
│   ├── section1_uc1_to_uc7.sql     # MySQL DB setup scripts (Section 1)
│   └── section2_uc8_to_uc12.sql    # ER Diagram / Normalization scripts (Section 2)
└── src/
    ├── main/
    │   ├── java/com/devraj/payroll/
    │   │   ├── db/         DBConnectionTest.java
    │   │   ├── exception/  PayrollDBException.java
    │   │   ├── model/      EmployeePayrollData.java, GenderStats.java
    │   │   └── service/    PayrollDBService.java
    │   └── resources/
    │       └── db.properties
    └── test/java/com/devraj/payroll/
        └── PayrollDBServiceTest.java
```

## Use Cases

### Section 1 — MySQL DB (run `sql/section1_uc1_to_uc7.sql`)
| UC | Description |
|----|-------------|
| UC1 | Create `payroll_service` database |
| UC2 | Create `employee_payroll` table |
| UC3 | INSERT employee data |
| UC4 | SELECT all employees |
| UC5 | SELECT by name + date range |
| UC6 | ALTER TABLE add gender + UPDATE |
| UC7 | Aggregate by gender (SUM/AVG/MIN/MAX/COUNT) |

### Section 2 — ER Diagram (run `sql/section2_uc8_to_uc12.sql`)
| UC | Description |
|----|-------------|
| UC8  | Add phone, address, department columns |
| UC9  | Add basic_pay, deductions, taxable_pay, income_tax, net_pay |
| UC10 | Terisa → Sales & Marketing + ER normalization |
| UC11 | Create normalized tables (employee, department, employee_department, payroll) |
| UC12 | Redo UC4/UC5/UC7 queries on normalized schema |

### Section 3 — JDBC (feature branches, merged to develop)
| UC | Branch | Description |
|----|--------|-------------|
| UC1 | feature/uc1-jdbc | Driver load, list drivers, connection |
| UC2 | feature/uc2-jdbc | Read all employee payroll data |
| UC3 | feature/uc3-jdbc | Update salary via Statement |
| UC4 | feature/uc4-jdbc | Update salary via PreparedStatement + Singleton refactor |
| UC5 | feature/uc5-jdbc | Date range query |
| UC6 | feature/uc6-jdbc | Gender aggregation stats |

## Setup

1. Run `sql/section1_uc1_to_uc7.sql` in MySQL Client
2. Run `sql/section2_uc8_to_uc12.sql` in MySQL Client
3. Update `src/main/resources/db.properties` with your MySQL password
4. Run: `mvn test`
