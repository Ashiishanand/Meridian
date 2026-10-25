package com.meridian.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Goal {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private int id;
    private int userId;
    private int goalTypeId;
    private String goalTypeName;
    private String name;
    private double targetHours;
    private double loggedHours;
    private LocalDate deadline;
    private GoalStatus status = GoalStatus.ACTIVE;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getGoalTypeId() { return goalTypeId; }
    public void setGoalTypeId(int goalTypeId) { this.goalTypeId = goalTypeId; }

    public String getGoalTypeName() { return goalTypeName; }
    public void setGoalTypeName(String goalTypeName) { this.goalTypeName = goalTypeName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetHours() { return targetHours; }
    public void setTargetHours(double targetHours) { this.targetHours = targetHours; }

    public double getLoggedHours() { return loggedHours; }
    public void setLoggedHours(double loggedHours) { this.loggedHours = loggedHours; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public GoalStatus getStatus() { return status; }
    public void setStatus(GoalStatus status) { this.status = status; }

    public int getProgressPercent() {
        if (targetHours <= 0) {
            return 0;
        }
        return (int) Math.min(100, Math.round(loggedHours / targetHours * 100));
    }

    public String getLoggedText() { return String.format("%.1f", loggedHours); }

    public String getTargetText() { return String.format("%.1f", targetHours); }

    public String getDeadlineText() { return deadline.format(DISPLAY); }

    public String getDeadlineNote() {
        if (status == GoalStatus.COMPLETED) {
            return "Completed";
        }
        long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        if (days < 0) {
            return "Overdue by " + (-days) + (days == -1 ? " day" : " days");
        }
        if (days == 0) {
            return "Due today";
        }
        return days + (days == 1 ? " day left" : " days left");
    }
}
