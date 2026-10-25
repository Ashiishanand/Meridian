package com.meridian.dao;

import com.meridian.exception.DataAccessException;
import com.meridian.model.Goal;
import com.meridian.model.GoalStatus;
import com.meridian.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

    public void create(int userId, int goalTypeId, String name, double targetHours, java.time.LocalDate deadline) {
        String sql = "INSERT INTO goals (user_id, goal_type_id, name, target_hours, deadline) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, goalTypeId);
            ps.setString(3, name);
            ps.setDouble(4, targetHours);
            ps.setDate(5, Date.valueOf(deadline));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not create goal", e);
        }
    }

    public List<Goal> findByUser(int userId) {
        return query(userId, false);
    }

    public List<Goal> findOpenByUser(int userId) {
        return query(userId, true);
    }

    public boolean delete(int goalId, int userId) {
        String sql = "DELETE FROM goals WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete goal", e);
        }
    }

    public int markOverdueGoals() {
        String sql = "UPDATE goals SET status = 'OVERDUE' WHERE status = 'ACTIVE' AND deadline < CURRENT_DATE";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            return st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("Could not update overdue goals", e);
        }
    }

    private List<Goal> query(int userId, boolean openOnly) {
        String sql = "SELECT g.id, g.user_id, g.goal_type_id, gt.name AS type_name, g.name, g.target_hours, "
                + "g.deadline, g.status, "
                + "COALESCE(SUM(TIMESTAMPDIFF(SECOND, t.start_time, t.end_time)), 0) / 3600 AS logged_hours "
                + "FROM goals g JOIN goal_types gt ON gt.id = g.goal_type_id "
                + "LEFT JOIN time_entries t ON t.goal_id = g.id "
                + "WHERE g.user_id = ?" + (openOnly ? " AND g.status <> 'COMPLETED'" : "")
                + " GROUP BY g.id, g.user_id, g.goal_type_id, gt.name, g.name, g.target_hours, g.deadline, g.status "
                + "ORDER BY g.deadline, g.id";
        List<Goal> goals = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapRow(rs));
                }
            }
            return goals;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load goals", e);
        }
    }

    private Goal mapRow(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setId(rs.getInt("id"));
        goal.setUserId(rs.getInt("user_id"));
        goal.setGoalTypeId(rs.getInt("goal_type_id"));
        goal.setGoalTypeName(rs.getString("type_name"));
        goal.setName(rs.getString("name"));
        goal.setTargetHours(rs.getDouble("target_hours"));
        goal.setLoggedHours(rs.getDouble("logged_hours"));
        goal.setDeadline(rs.getDate("deadline").toLocalDate());
        goal.setStatus(GoalStatus.valueOf(rs.getString("status")));
        return goal;
    }
}
