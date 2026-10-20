package com.meridian.dao;

import com.meridian.model.TimeEntry;
import com.meridian.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Stores time spent working toward a user's own goals. */
public class TimeEntryDAO {
    public List<TimeEntry> findByUser(long userId) throws SQLException {
        String sql = "SELECT t.entry_id,t.goal_id,g.title,t.start_time,t.end_time,t.notes," +
                "TIMESTAMPDIFF(MINUTE,t.start_time,t.end_time) AS minutes " +
                "FROM time_entries t JOIN goals g ON g.goal_id=t.goal_id " +
                "WHERE t.user_id=? ORDER BY t.start_time DESC";
        List<TimeEntry> entries = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) entries.add(new TimeEntry(
                        r.getLong("entry_id"), r.getLong("goal_id"), r.getString("title"),
                        r.getTimestamp("start_time").toLocalDateTime(),
                        r.getTimestamp("end_time").toLocalDateTime(),
                        r.getString("notes"), r.getLong("minutes")));
            }
        }
        return entries;
    }

    public void create(long userId, long goalId, LocalDateTime start, LocalDateTime end, String notes) throws SQLException {
        if (start == null || end == null || !end.isAfter(start))
            throw new IllegalArgumentException("End time must be later than start time.");
        String sql = "INSERT INTO time_entries(user_id,goal_id,start_time,end_time,notes) " +
                "SELECT ?,goal_id,?,?,? FROM goals WHERE goal_id=? AND user_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, userId);
            p.setTimestamp(2, Timestamp.valueOf(start));
            p.setTimestamp(3, Timestamp.valueOf(end));
            p.setString(4, notes == null ? "" : notes.trim());
            p.setLong(5, goalId);
            p.setLong(6, userId);
            if (p.executeUpdate() == 0) throw new IllegalArgumentException("Choose one of your own goals.");
        }
    }

    public void delete(long userId, long entryId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM time_entries WHERE entry_id=? AND user_id=?")) {
            p.setLong(1, entryId);
            p.setLong(2, userId);
            if (p.executeUpdate() == 0) throw new SQLException("Entry not found.");
        }
    }

    public long totalMinutes(long userId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE,start_time,end_time)),0) FROM time_entries WHERE user_id=?")) {
            p.setLong(1, userId);
            try (ResultSet r = p.executeQuery()) { r.next(); return r.getLong(1); }
        }
    }
}
