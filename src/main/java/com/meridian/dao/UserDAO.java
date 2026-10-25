package com.meridian.dao;

import com.meridian.exception.DataAccessException;
import com.meridian.exception.DuplicateEmailException;
import com.meridian.model.Admin;
import com.meridian.model.RegularUser;
import com.meridian.model.Role;
import com.meridian.model.User;
import com.meridian.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not look up user by email", e);
        }
    }

    public boolean existsAdmin() {
        return count("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'") > 0;
    }

    public int countUsers() {
        return count("SELECT COUNT(*) FROM users WHERE role = 'USER'");
    }

    public int countActiveUsers() {
        return count("SELECT COUNT(*) FROM users WHERE role = 'USER' AND is_active = TRUE");
    }

    public int create(User user) throws DuplicateEmailException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName().trim());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
            return user.getId();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEmailException(user.getEmail());
            }
            throw new DataAccessException("Could not create user", e);
        }
    }

    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update last login", e);
        }
    }

    public boolean setActive(int id, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ? AND role = 'USER'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update account status", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'USER'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete user", e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, created_at DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Could not list users", e);
        }
    }

    private int count(String sql) {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not run count query", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String hash = rs.getString("password_hash");
        Role role = Role.valueOf(rs.getString("role"));

        User user = (role == Role.ADMIN)
                ? new Admin(id, name, email, hash)
                : new RegularUser(id, name, email, hash);
        user.setActive(rs.getBoolean("is_active"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            user.setCreatedAt(created.toLocalDateTime());
        }
        Timestamp last = rs.getTimestamp("last_login");
        if (last != null) {
            user.setLastLogin(last.toLocalDateTime());
        }
        return user;
    }
}
