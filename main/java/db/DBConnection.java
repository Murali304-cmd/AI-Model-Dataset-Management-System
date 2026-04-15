package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ── Change only these 3 values ──────────────────────────────────────────
    private static final String HOST = "localhost";
    private static final String PORT = "3306";        // use the port from Step 3
    private static final String DB   = "ai_dataset_db";
    private static final String USER = "root";        // your MySQL username
    private static final String PASS = "dbms";            // your MySQL password (blank if none)
    // ────────────────────────────────────────────────────────────────────────

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "JDBC Driver not found! Add mysql-connector-java JAR to Libraries.", e);
        }
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("=== DB CONNECTION FAILED ===");
            System.err.println("URL  : " + URL);
            System.err.println("USER : " + USER);
            System.err.println("ERROR: " + e.getMessage());
            System.err.println("============================");
            throw e;
        }
    }

    // ── Run this file alone first to confirm connection works ────────────────
    public static void main(String[] args) {
        System.out.println("Testing connection to MySQL...");
        System.out.println("URL: " + URL);
        try (Connection con = getConnection()) {
            System.out.println("\n✅ SUCCESS! Connected to MySQL.");
            System.out.println("   Database : " + con.getCatalog());
            System.out.println("   Server   : " + con.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("\n❌ FAILED: " + e.getMessage());
            System.err.println("\nDiagnosis:");
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("communications link failure") || msg.contains("connection refused")) {
                System.err.println("  → MySQL service is NOT running.");
                System.err.println("  → Fix: Open Command Prompt as Admin → type: net start MySQL57");
            } else if (msg.contains("access denied")) {
                System.err.println("  → Wrong username or password.");
                System.err.println("  → Fix: Update USER and PASS in DBConnection.java");
            } else if (msg.contains("unknown database")) {
                System.err.println("  → Database 'ai_dataset_db' does not exist.");
                System.err.println("  → Fix: Run  CREATE DATABASE ai_dataset_db;  in MySQL");
            } else if (msg.contains("classnotfound") || msg.contains("driver")) {
                System.err.println("  → JAR file missing from project Libraries.");
                System.err.println("  → Fix: Right-click project → Properties → Libraries → Add JAR");
            }
        }
    }
}