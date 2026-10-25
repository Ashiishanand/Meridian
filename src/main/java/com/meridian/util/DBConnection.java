package com.meridian.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            PROPS.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() { }

    private static String resolve(String value) {
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String env = System.getenv(value.substring(2, value.length() - 1));
            return env == null ? "" : env;
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                resolve(PROPS.getProperty("db.url")),
                resolve(PROPS.getProperty("db.user")),
                resolve(PROPS.getProperty("db.password")));
    }
}