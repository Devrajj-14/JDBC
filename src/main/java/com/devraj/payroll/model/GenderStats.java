package com.devraj.payroll.model;

/**
 * [Devraj]: UC6 - GenderStats DTO
 * Holds aggregated payroll statistics grouped by gender.
 * Used instead of returning a raw ResultSet for proper resource management.
 */
public class GenderStats {

    public String gender;
    public long   empCount;
    public double totalSalary;
    public double avgSalary;
    public double minSalary;
    public double maxSalary;

    // [Devraj]: Constructor to populate gender aggregate statistics from ResultSet
    public GenderStats(String gender, long empCount, double totalSalary,
                       double avgSalary, double minSalary, double maxSalary) {
        this.gender      = gender;
        this.empCount    = empCount;
        this.totalSalary = totalSalary;
        this.avgSalary   = avgSalary;
        this.minSalary   = minSalary;
        this.maxSalary   = maxSalary;
    }

    @Override
    public String toString() {
        // [Devraj]: Formatted output for gender statistics logging
        return "GenderStats{" +
                "gender='" + gender + '\'' +
                ", count=" + empCount +
                ", total=" + totalSalary +
                ", avg="   + avgSalary +
                ", min="   + minSalary +
                ", max="   + maxSalary +
                '}';
    }
}
