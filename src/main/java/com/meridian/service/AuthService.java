package com.meridian.service;
import com.meridian.model.User;
import com.meridian.util.DatabaseConnection;
import com.meridian.util.PasswordUtil;
import java.sql.*;
import java.util.regex.Pattern;

/** Authentication and registration logic; the first registered account becomes the initial admin. */
public class AuthService {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    public User register(String name, String email, char[] password) throws SQLException {
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase();
        if (cleanName.length() < 2 || cleanName.length() > 100) throw new IllegalArgumentException("Name must contain between 2 and 100 characters.");
        if (!EMAIL.matcher(cleanEmail).matches()) throw new IllegalArgumentException("Enter a valid email address.");
        if (password == null || password.length < 8) throw new IllegalArgumentException("Password must contain at least 8 characters.");
        PasswordUtil.PasswordData data = PasswordUtil.hash(password);
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String role;
                try (PreparedStatement count = connection.prepareStatement("SELECT COUNT(*) FROM users"); ResultSet rs = count.executeQuery()) {
                    rs.next(); role = rs.getLong(1) == 0 ? "ADMIN" : "USER";
                }
                String sql = "INSERT INTO users(full_name,email,password_hash,password_salt,role) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, cleanName); ps.setString(2, cleanEmail); ps.setString(3, data.hash()); ps.setString(4, data.salt()); ps.setString(5, role);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Could not create account.");
                        User user = new User(keys.getLong(1), cleanName, cleanEmail, role, "ACTIVE");
                        connection.commit(); return user;
                    }
                }
            } catch (SQLException | RuntimeException ex) { connection.rollback(); throw ex; }
        }
    }
    public User login(String email, char[] password) throws SQLException {
        String sql = "SELECT user_id,full_name,email,password_hash,password_salt,role,account_status FROM users WHERE email=?";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email == null ? "" : email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                if (!"ACTIVE".equals(rs.getString("account_status"))) throw new IllegalArgumentException("This account is inactive. Contact an administrator.");
                if (!PasswordUtil.verify(password, rs.getString("password_hash"), rs.getString("password_salt"))) return null;
                return new User(rs.getLong("user_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("role"), rs.getString("account_status"));
            }
        }
    }
}
