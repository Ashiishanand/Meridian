package com.meridian.dao;

import com.meridian.exception.DataAccessException;
import com.meridian.exception.InvalidTimeEntryException;
import com.meridian.exception.OverlappingTimeEntryException;
import com.meridian.model.GoalStatus;
import com.meridian.model.TimeEntry;
import com.meridian.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TimeEntryDAO {

    public List<TimeEntry> findByUser(int userId) {
        String sql = "SELECT t.id, t.goal_id, g.name AS goal_name, t.start_time, t.end_time, t.notes "
                + "FROM time_entries t JOIN goals g ON g.id = t.goal_id "
                + "WHERE t.user_id = ? ORDER BY t.start_time DESC";
        List<TimeEntry> entries = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimeEntry entry = new TimeEntry();
                    entry.setId(rs.getInt("id"));
                    entry.setGoalId(rs.getInt("goal_id"));
                    entry.setGoalName(rs.getString("goal_name"));
                    entry.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    entry.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    entry.setNotes(rs.getString("notes"));
                    entries.add(entry);
                }
            }
            return entries;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load time entries", e);
        }
    }

    public boolean log(int userId, int goalId, LocalDateTime start, LocalDateTime end, String notes)
            throws InvalidTimeEntryException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                lockGoal(con, userId, goalId);
                checkOverlap(con, userId, start, end);
                insertEntry(con, userId, goalId, start, end, notes);
                boolean completed = refreshStatus(con, goalId);
                con.commit();
                return completed;
            } catch (InvalidTimeEntryException | SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save time entry", e);
        }
    }

    public boolean delete(int entryId, int userId) {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int goalId = findGoalId(con, entryId, userId);
                if (goalId < 0) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM time_entries WHERE id = ?")) {
                    ps.setInt(1, entryId);
                    ps.executeUpdate();
                }
                refreshStatus(con, goalId);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete time entry", e);
        }
    }

    public double totalHours(int userId) {
        String sql = "SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) / 60 "
                + "FROM time_entries WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not calculate total hours", e);
        }
    }

    public Set<LocalDate> loggedDates(int userId) {
        String sql = "SELECT DISTINCT DATE(start_time) AS d FROM time_entries WHERE user_id = ?";
        Set<LocalDate> dates = new HashSet<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate("d").toLocalDate());
                }
            }
            return dates;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load logged dates", e);
        }
    }

    public Map<String, Double> hoursPerDay(int userId, int days) {
        LocalDate first = LocalDate.now().minusDays(days - 1);
        Map<LocalDate, Double> totals = new HashMap<>();
        String sql = "SELECT DATE(start_time) AS d, SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)) AS m "
                + "FROM time_entries WHERE user_id = ? AND start_time >= ? GROUP BY DATE(start_time)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(first.atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totals.put(rs.getDate("d").toLocalDate(), rs.getDouble("m") / 60);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load daily hours", e);
        }

        DateTimeFormatter label = DateTimeFormatter.ofPattern("dd MMM");
        Map<String, Double> result = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = first.plusDays(i);
            double hours = Math.round(totals.getOrDefault(day, 0.0) * 10) / 10.0;
            result.put(day.format(label), hours);
        }
        return result;
    }

    private void lockGoal(Connection con, int userId, int goalId)
            throws SQLException, InvalidTimeEntryException {
        String sql = "SELECT status FROM goals WHERE id = ? AND user_id = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new InvalidTimeEntryException("The selected goal was not found.");
                }
                if (GoalStatus.valueOf(rs.getString("status")) == GoalStatus.COMPLETED) {
                    throw new InvalidTimeEntryException("This goal is already completed.");
                }
            }
        }
    }

    private void checkOverlap(Connection con, int userId, LocalDateTime start, LocalDateTime end)
            throws SQLException, OverlappingTimeEntryException {
        String sql = "SELECT COUNT(*) FROM time_entries WHERE user_id = ? AND start_time < ? AND end_time > ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new OverlappingTimeEntryException();
                }
            }
        }
    }

    private void insertEntry(Connection con, int userId, int goalId, LocalDateTime start,
                             LocalDateTime end, String notes) throws SQLException {
        String sql = "INSERT INTO time_entries (goal_id, user_id, start_time, end_time, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ps.setTimestamp(4, Timestamp.valueOf(end));
            ps.setString(5, notes);
            ps.executeUpdate();
        }
    }

    private int findGoalId(Connection con, int entryId, int userId) throws SQLException {
        String sql = "SELECT goal_id FROM time_entries WHERE id = ? AND user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    private boolean refreshStatus(Connection con, int goalId) throws SQLException {
        String select = "SELECT g.target_hours, g.deadline, "
                + "COALESCE((SELECT SUM(TIMESTAMPDIFF(SECOND, t.start_time, t.end_time)) "
                + "FROM time_entries t WHERE t.goal_id = g.id), 0) / 3600 AS logged "
                + "FROM goals g WHERE g.id = ?";
        GoalStatus status;
        try (PreparedStatement ps = con.prepareStatement(select)) {
            ps.setInt(1, goalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                double target = rs.getDouble("target_hours");
                double logged = rs.getDouble("logged");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();
                if (logged >= target) {
                    status = GoalStatus.COMPLETED;
                } else if (deadline.isBefore(LocalDate.now())) {
                    status = GoalStatus.OVERDUE;
                } else {
                    status = GoalStatus.ACTIVE;
                }
            }
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE goals SET status = ? WHERE id = ?")) {
            ps.setString(1, status.name());
            ps.setInt(2, goalId);
            ps.executeUpdate();
        }
        return status == GoalStatus.COMPLETED;
    }
}
