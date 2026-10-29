package com.bloodbank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // On the server these come from environment variables; locally the defaults below are used.
    private static final String URL  = env("DB_URL",  "jdbc:mysql://localhost:3306/bloodbank");
    private static final String USER = env("DB_USER", "root");
    private static final String PASS = env("DB_PASS", "root");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
