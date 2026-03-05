package com.devraj.payroll;

import com.devraj.payroll.model.EmployeePayrollData;
import com.devraj.payroll.model.GenderStats;
import com.devraj.payroll.service.PayrollDBService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [Devraj]: JUnit 5 tests for PayrollDBService
 * Covers UC2 (read), UC3/UC4 (update + sync), UC5 (date range), UC6 (gender stats)
 */
class PayrollDBServiceTest {

    // [Devraj]: Singleton service reused across all tests
    private final PayrollDBService service = PayrollDBService.getInstance();

    // ─────────────────────────────────────────────────────────────────────────
    // UC2 — Verify read returns non-empty list
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void givenPayrollDB_whenRead_shouldReturnNonEmptyList() {
        // [Devraj]: UC2 test — ensure at least one employee record is returned
        List<EmployeePayrollData> list = service.readEmployeePayrollData();
        assertNotNull(list, "Result list should not be null");
        assertTrue(list.size() > 0, "Result list should have at least one employee");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC3 — Update salary using Statement, compare with DB
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void givenTerisa_whenUpdateSalaryViaStatement_shouldSyncWithDB() {
        // [Devraj]: UC3 test — Statement-based update; rows > 0 confirms DB write
        int rows = service.updateSalary("Terisa", 3000000.00);
        assertTrue(rows >= 1, "At least one row should be updated");

        // [Devraj]: Verify DB now reflects the updated salary
        List<EmployeePayrollData> result = service.readByName("Terisa");
        assertTrue(result.stream().allMatch(e -> e.salary == 3000000.00),
                "All Terisa records should show updated salary 3000000.00");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC4 — Update salary using PreparedStatement, compare with DB
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void givenTerisa_whenUpdateSalaryViaPreparedStatement_shouldSyncWithDB() {
        // [Devraj]: UC4 test — PreparedStatement update, same expected outcome as UC3
        int rows = service.updateSalaryPrepared("Terisa", 3000000.00);
        assertTrue(rows >= 1, "At least one row should be updated via PreparedStatement");

        // [Devraj]: Compare employee object salary with DB record — must match
        List<EmployeePayrollData> result = service.readByName("Terisa");
        assertTrue(result.stream().allMatch(e -> e.salary == 3000000.00),
                "Terisa's salary in DB should be 3000000.00 after PreparedStatement update");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC5 — Retrieve employees in a date range
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void givenDateRange_whenReadJoinedBetween_shouldReturnMatchingEmployees() {
        // [Devraj]: UC5 test — employees who joined between 2018-01-01 and today
        LocalDate from = LocalDate.of(2018, 1, 1);
        LocalDate to   = LocalDate.now();

        List<EmployeePayrollData> result = service.readJoinedBetween(from, to);
        assertNotNull(result, "Date-range result should not be null");
        // [Devraj]: Each returned employee's start must be within the given range
        result.forEach(e ->
            assertTrue(!e.start.isBefore(from) && !e.start.isAfter(to),
                "Employee " + e.name + " start date " + e.start + " must be within range"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC6 — Gender statistics aggregation
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void givenPayrollDB_whenGetGenderStats_shouldReturnAggregates() {
        // [Devraj]: UC6 test — verify non-empty stats and non-negative salary aggregates
        List<GenderStats> stats = service.getGenderStats();
        assertNotNull(stats, "Gender stats should not be null");
        assertTrue(stats.size() > 0, "There should be at least one gender group");

        stats.forEach(s -> {
            // [Devraj]: Sanity check: aggregate salary values should be positive
            assertTrue(s.totalSalary >= 0, "Total salary must be non-negative for gender: " + s.gender);
            assertTrue(s.empCount    >= 0, "Employee count must be non-negative for gender: " + s.gender);
        });
    }
}
