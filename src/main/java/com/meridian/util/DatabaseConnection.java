package com.meridian.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Creates JDBC connections using environment variables when provided. */
public final class DatabaseConnection {
    private static final String URL = env("MERIDIAN_DB_URL", "jdbc:mysql://localhost:3306/meridian_db?useSSL=false&serverTimezone=UTC");
    private static final String USER = env("MERIDIAN_DB_USER", "root");
    private static final String PASSWORD = env("MERIDIAN_DB_PASSWORD", "");
    private DatabaseConnection() { }
    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
