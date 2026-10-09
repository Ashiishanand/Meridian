package com.meridian.dao;
import com.meridian.model.Task;
import com.meridian.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** SQL operations for tasks, always scoped to the owning user. */
public class TaskDAO {
    public List<Task> findByUser(long userId)throws SQLException{
        String sql="SELECT task_id,user_id,goal_id,title,description,due_date,priority,status FROM tasks WHERE user_id=? ORDER BY due_date IS NULL,due_date";
        List<Task> tasks=new ArrayList<>();
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())tasks.add(map(r));}
        }return tasks;
    }
    public void create(long userId,Long goalId,String title,String description,LocalDate dueDate,String priority)throws SQLException{
        validate(title,priority);
        if(goalId!=null){
            try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT 1 FROM goals WHERE goal_id=? AND user_id=?")){
                p.setLong(1,goalId);p.setLong(2,userId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Choose one of your own goals.");}
            }
        }
        String sql="INSERT INTO tasks(user_id,goal_id,title,description,due_date,priority) VALUES(?,?,?,?,?,?)";
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setLong(1,userId);if(goalId==null)p.setNull(2,Types.BIGINT);else p.setLong(2,goalId);
            p.setString(3,title.trim());p.setString(4,description);if(dueDate==null)p.setNull(5,Types.DATE);else p.setDate(5,Date.valueOf(dueDate));
            p.setString(6,priority);p.executeUpdate();
        }
    }
    public void updateStatus(long userId,long taskId,String status)throws SQLException{
        if(!List.of("PENDING","IN_PROGRESS","COMPLETED").contains(status))throw new IllegalArgumentException("Invalid task status.");
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("UPDATE tasks SET status=? WHERE task_id=? AND user_id=?")){
            p.setString(1,status);p.setLong(2,taskId);p.setLong(3,userId);if(p.executeUpdate()==0)throw new SQLException("Task not found or access denied.");
        }
    }
    public void delete(long userId,long taskId)throws SQLException{
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM tasks WHERE task_id=? AND user_id=?")){
            p.setLong(1,taskId);p.setLong(2,userId);if(p.executeUpdate()==0)throw new SQLException("Task not found or access denied.");
        }
    }
    private static void validate(String title,String priority){
        if(title==null||title.trim().isEmpty()||title.trim().length()>150)throw new IllegalArgumentException("Task title is required (maximum 150 characters).");
        if(!List.of("LOW","MEDIUM","HIGH").contains(priority))throw new IllegalArgumentException("Choose a valid priority.");
    }
    private static Task map(ResultSet r)throws SQLException{
        long goal=r.getLong("goal_id");Long goalId=r.wasNull()?null:goal;Date due=r.getDate("due_date");
        return new Task(r.getLong("task_id"),r.getLong("user_id"),goalId,r.getString("title"),r.getString("description"),
          due==null?null:due.toLocalDate(),r.getString("priority"),r.getString("status"));
    }
}
