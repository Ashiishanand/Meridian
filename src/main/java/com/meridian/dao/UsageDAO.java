package com.meridian.dao;

import com.meridian.exception.DataAccessException;
import com.meridian.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsageDAO {

    public void log(int userId, String action) {
        String sql = "INSERT INTO activity_log (user_id, action) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not write activity log", e);
        }
    }

    public Map<String, Integer> loginsPerDay(int days) {
        LocalDate start = LocalDate.now().minusDays(days - 1);
        Map<LocalDate, Integer> counts = new HashMap<>();
        String sql = "SELECT DATE(created_at) AS d, COUNT(*) AS c FROM activity_log "
                + "WHERE action = 'LOGIN' AND created_at >= ? GROUP BY DATE(created_at)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getDate("d").toLocalDate(), rs.getInt("c"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load login statistics", e);
        }

        DateTimeFormatter label = DateTimeFormatter.ofPattern("dd MMM");
        Map<String, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = start.plusDays(i);
            result.put(day.format(label), counts.getOrDefault(day, 0));
        }
        return result;
    }

    public Map<String, Integer> actionCounts() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT action, COUNT(*) AS c FROM activity_log GROUP BY action ORDER BY c DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("action"), rs.getInt("c"));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load activity statistics", e);
        }
    }

    public int totalGoals() {
        return count("SELECT COUNT(*) FROM goals");
    }

    public int totalTimeEntries() {
        return count("SELECT COUNT(*) FROM time_entries");
    }

    public double totalHours() {
        String sql = "SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) / 60 FROM time_entries";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not calculate total hours", e);
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
}
