package com.devraj.payroll.service;

import com.devraj.payroll.exception.PayrollDBException;
import com.devraj.payroll.model.EmployeePayrollData;
import com.devraj.payroll.model.GenderStats;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * [Devraj]: Singleton service for all Employee Payroll DB operations.
 * Covers UC2 through UC6 of the JDBC section:
 *   UC2 - Read all employee payroll data
 *   UC3 - Update salary using Statement (SQL injection prone, educational)
 *   UC4 - Update salary using PreparedStatement + readByName + Singleton refactor
 *   UC5 - Read employees joined in a date range
 *   UC6 - Aggregate statistics by gender (SUM/AVG/MIN/MAX/COUNT)
 */
public class PayrollDBService {

    // [Devraj]: Singleton instance — ensures PreparedStatements are cached at program level
    private static PayrollDBService instance;

    // [Devraj]: DB connection parameters loaded once (read from db.properties in production)
    private final String url  = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&allowPublicKeyRetrieval=true";
    private final String user = "root";
    private final String pass = "root";

    // [Devraj]: Private constructor enforces singleton pattern
    private PayrollDBService() {}

    /**
     * [Devraj]: UC4 refactor — returns the single shared instance of PayrollDBService
     */
    public static PayrollDBService getInstance() {
        if (instance == null) {
            instance = new PayrollDBService();
        }
        return instance;
    }

    /**
     * [Devraj]: Opens a fresh JDBC connection for each operation.
     * Used inside try-with-resources so connections are always closed properly.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC2 — Read all employee payroll data
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * [Devraj]: UC2 - Reads all rows from employee_payroll and returns them as a list.
     * Uses Statement for a straightforward full-table scan.
     */
    public List<EmployeePayrollData> readEmployeePayrollData() {
        // [Devraj]: SELECT all columns needed to populate EmployeePayrollData
        String sql = "SELECT id, name, salary, start FROM employee_payroll";
        List<EmployeePayrollData> list = new ArrayList<>();

        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // [Devraj]: Map each row to an EmployeePayrollData object
                list.add(buildFromResultSet(rs));
            }
            return list;

        } catch (SQLException e) {
            // [Devraj]: Wrap SQLException in custom exception per UC2 requirements
            throw new PayrollDBException("Failed to read employee payroll data", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC3 — Update salary using plain Statement (educational — shows SQL injection risk)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * [Devraj]: UC3 - Updates employee salary using a plain Statement.
     * WARNING: String concatenation makes this vulnerable to SQL injection.
     * This method exists to demonstrate why PreparedStatement (UC4) is preferred.
     *
     * @return number of rows updated
     */
    public int updateSalary(String name, double newSalary) {
        // [Devraj]: String-concatenated SQL — intentionally insecure for UC3 demo
        String sql = "UPDATE employee_payroll SET salary = " + newSalary
                   + " WHERE name = '" + name + "'";

        try (Connection con = getConnection();
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql);

        } catch (SQLException e) {
            // [Devraj]: Wrap with custom exception as required by UC3
            throw new PayrollDBException("Failed to update salary via Statement", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC4 — Update salary using PreparedStatement (safe, cached, reusable)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * [Devraj]: UC4 - Updates employee salary using PreparedStatement.
     * PreparedStatement is SQL-injection-safe and benefits from driver/DB-level caching.
     *
     * @return number of rows updated
     */
    public int updateSalaryPrepared(String name, double newSalary) {
        // [Devraj]: Parameterised SQL prevents injection and enables caching
        String sql = "UPDATE employee_payroll SET salary = ? WHERE name = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newSalary);
            ps.setString(2, name);
            return ps.executeUpdate();

        } catch (SQLException e) {
            // [Devraj]: Custom exception wraps SQL error per UC4 requirements
            throw new PayrollDBException("Failed to update salary via PreparedStatement", e);
        }
    }

    /**
     * [Devraj]: UC4 refactor - Retrieves employee payroll data filtered by name.
     * Uses PreparedStatement for caching and safety.
     *
     * @param name employee name to search for
     * @return list of matching EmployeePayrollData records
     */
    public List<EmployeePayrollData> readByName(String name) {
        // [Devraj]: Parameterised SELECT for safe, cacheable retrieval by name
        String sql = "SELECT id, name, salary, start FROM employee_payroll WHERE name = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                List<EmployeePayrollData> list = new ArrayList<>();
                while (rs.next()) {
                    // [Devraj]: Reuse shared helper to populate EmployeePayrollData from ResultSet
                    list.add(buildFromResultSet(rs));
                }
                return list;
            }

        } catch (SQLException e) {
            // [Devraj]: Custom exception wraps SQL error per UC4 refactor requirements
            throw new PayrollDBException("Failed to read employee payroll data by name", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC5 — Retrieve employees who joined in a given date range
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * [Devraj]: UC5 - Returns employees whose start date falls within [from, to].
     * Uses SQL BETWEEN for inclusive date range filtering.
     *
     * @param from start of date range (inclusive)
     * @param to   end of date range (inclusive)
     * @return list of employees within the date range
     */
    public List<EmployeePayrollData> readJoinedBetween(LocalDate from, LocalDate to) {
        // [Devraj]: BETWEEN clause covers UC5 requirement for joined-in-date-range query
        String sql = "SELECT id, name, salary, start FROM employee_payroll " +
                     "WHERE start BETWEEN ? AND ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // [Devraj]: Convert LocalDate to java.sql.Date for JDBC compatibility
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                List<EmployeePayrollData> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(buildFromResultSet(rs));
                }
                return list;
            }

        } catch (SQLException e) {
            // [Devraj]: Custom exception wraps SQL error per UC5 requirements
            throw new PayrollDBException("Failed to read employees by date range", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC6 — Aggregate salary statistics grouped by gender
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * [Devraj]: UC6 - Computes SUM, AVG, MIN, MAX, COUNT of salaries grouped by gender.
     * Returns a GenderStats DTO list to avoid leaking unclosed ResultSet resources.
     *
     * @return list of GenderStats, one entry per gender value in the table
     */
    public List<GenderStats> getGenderStats() {
        // [Devraj]: Aggregate query per UC6 — GROUP BY gender with all required functions
        String sql = """
                SELECT gender,
                       COUNT(*)     AS emp_count,
                       SUM(salary)  AS total_salary,
                       AVG(salary)  AS avg_salary,
                       MIN(salary)  AS min_salary,
                       MAX(salary)  AS max_salary
                FROM employee_payroll
                GROUP BY gender
                """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<GenderStats> stats = new ArrayList<>();
            while (rs.next()) {
                // [Devraj]: Map each group row to a GenderStats DTO (safe resource closure)
                stats.add(new GenderStats(
                        rs.getString("gender"),
                        rs.getLong("emp_count"),
                        rs.getDouble("total_salary"),
                        rs.getDouble("avg_salary"),
                        rs.getDouble("min_salary"),
                        rs.getDouble("max_salary")
                ));
            }
            return stats;

        } catch (SQLException e) {
            // [Devraj]: Custom exception wraps SQL error per UC6 requirements
            throw new PayrollDBException("Failed to compute gender statistics", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shared helper
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * [Devraj]: Helper — maps current ResultSet row to an EmployeePayrollData object.
     * Centralises column-to-field mapping so it is reused across UC2, UC4, and UC5.
     */
    private EmployeePayrollData buildFromResultSet(ResultSet rs) throws SQLException {
        return new EmployeePayrollData(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("salary"),
                rs.getDate("start").toLocalDate()     // [Devraj]: Convert SQL Date → LocalDate
        );
    }
}
