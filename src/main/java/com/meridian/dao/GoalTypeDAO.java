package com.meridian.dao;

import com.meridian.exception.DataAccessException;
import com.meridian.exception.DuplicateEntryException;
import com.meridian.model.GoalType;
import com.meridian.model.TrackingMetric;
import com.meridian.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GoalTypeDAO {

    public List<GoalType> findAll() {
        List<GoalType> list = new ArrayList<>();
        String sql = "SELECT * FROM goal_types ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new GoalType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        TrackingMetric.valueOf(rs.getString("tracking_metric")),
                        rs.getBoolean("is_active")));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not list goal types", e);
        }
    }

    public List<GoalType> findActive() {
        List<GoalType> active = new ArrayList<>();
        for (GoalType type : findAll()) {
            if (type.isActive()) {
                active.add(type);
            }
        }
        return active;
    }

    public void create(String name, String description, TrackingMetric metric) throws DuplicateEntryException {
        String sql = "INSERT INTO goal_types (name, description, tracking_metric) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, description == null ? "" : description.trim());
            ps.setString(3, metric.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateEntryException("A goal type named '" + name.trim() + "' already exists.");
            }
            throw new DataAccessException("Could not create goal type", e);
        }
    }

    public boolean setActive(int id, boolean active) {
        String sql = "UPDATE goal_types SET is_active = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update goal type", e);
        }
    }

    public boolean isInUse(int id) {
        String sql = "SELECT COUNT(*) FROM goals WHERE goal_type_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check goal type usage", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM goal_types WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete goal type", e);
        }
    }
}
