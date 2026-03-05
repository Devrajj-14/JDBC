package com.devraj.payroll.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * [Devraj]: UC1 - JDBC Connectivity Test
 * Demonstrates driver loading, driver listing, and connection establishment
 * to the payroll_service MySQL database.
 */
public class DBConnectionTest {

    public static void main(String[] args) throws Exception {

        // [Devraj]: Step 1 - Check if MySQL JDBC driver class is loaded
        String driverClass = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverClass);
        System.out.println("Driver class loaded: " + driverClass);

        // [Devraj]: Step 2 - List all MySQL JDBC Drivers registered with DriverManager
        System.out.println("\n--- Registered JDBC Drivers ---");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            System.out.println("Driver: " + drivers.nextElement().getClass().getName());
        }

        // [Devraj]: Step 3 - Establish connection to payroll_service database
        String url  = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "root";

        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            // [Devraj]: Step 4 - Confirm connection is established (expected output: true)
            System.out.println("\nConnection Established: " + (con != null));
        }
    }
}
