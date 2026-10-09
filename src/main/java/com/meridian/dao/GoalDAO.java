package com.meridian.dao;
import com.meridian.model.Goal;
import com.meridian.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** SQL operations for goals. Every user-facing query is scoped to the owner. */
public class GoalDAO {
    public List<Goal> findByUser(long userId) throws SQLException {
        String sql="SELECT goal_id,user_id,title,description,start_date,target_date,status FROM goals WHERE user_id=? ORDER BY target_date";
        List<Goal> goals=new ArrayList<>();
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())goals.add(map(r));}
        } return goals;
    }
    public void create(long userId,String title,String description,LocalDate start,LocalDate target) throws SQLException {
        validate(title,start,target);
        String sql="INSERT INTO goals(user_id,title,description,start_date,target_date) VALUES(?,?,?,?,?)";
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,userId);p.setString(2,title.trim());p.setString(3,description);p.setDate(4,Date.valueOf(start));p.setDate(5,Date.valueOf(target));p.executeUpdate();
        }
    }
    public void updateStatus(long userId,long goalId,String status) throws SQLException {
        if(!List.of("NOT_STARTED","IN_PROGRESS","COMPLETED","ON_HOLD").contains(status))throw new IllegalArgumentException("Invalid goal status.");
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE goals SET status=? WHERE goal_id=? AND user_id=?")){
            p.setString(1,status);p.setLong(2,goalId);p.setLong(3,userId);if(p.executeUpdate()==0)throw new SQLException("Goal not found or access denied.");
        }
    }
    public void delete(long userId,long goalId) throws SQLException {
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM goals WHERE goal_id=? AND user_id=?")){
            p.setLong(1,goalId);p.setLong(2,userId);if(p.executeUpdate()==0)throw new SQLException("Goal not found or access denied.");
        }
    }
    private static void validate(String title,LocalDate start,LocalDate target){
        if(title==null||title.trim().isEmpty()||title.trim().length()>150)throw new IllegalArgumentException("Goal title is required (maximum 150 characters).");
        if(start==null||target==null||target.isBefore(start))throw new IllegalArgumentException("Target date must be on or after the start date.");
    }
    private static Goal map(ResultSet r)throws SQLException{
        return new Goal(r.getLong("goal_id"),r.getLong("user_id"),r.getString("title"),r.getString("description"),
          r.getDate("start_date").toLocalDate(),r.getDate("target_date").toLocalDate(),r.getString("status"));
    }
}
