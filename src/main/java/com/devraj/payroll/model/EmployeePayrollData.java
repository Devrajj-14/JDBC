package com.devraj.payroll.model;

import java.time.LocalDate;

/**
 * [Devraj]: UC2 - EmployeePayrollData model class
 * Represents a single employee payroll record fetched from the database.
 * Includes id, name, salary, and start date for backward compatibility.
 */
public class EmployeePayrollData {

    public int id;
    public String name;
    public double salary;
    public LocalDate start;

    // [Devraj]: Constructor for building employee payroll objects from DB result sets
    public EmployeePayrollData(int id, String name, double salary, LocalDate start) {
        this.id     = id;
        this.name   = name;
        this.salary = salary;
        this.start  = start;
    }

    @Override
    public String toString() {
        // [Devraj]: Human-readable representation of payroll data for logging/debugging
        return "EmployeePayrollData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", start=" + start +
                '}';
    }
}
