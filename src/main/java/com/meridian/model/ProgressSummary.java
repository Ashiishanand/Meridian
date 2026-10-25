package com.meridian.model;

import java.util.List;
import java.util.Map;

public class ProgressSummary {

    private final List<Goal> goals;
    private final int activeGoals;
    private final int completedGoals;
    private final int overdueGoals;
    private final double totalHours;
    private final int streak;
    private final Map<String, Double> hoursPerDay;

    public ProgressSummary(List<Goal> goals, int activeGoals, int completedGoals, int overdueGoals,
                           double totalHours, int streak, Map<String, Double> hoursPerDay) {
        this.goals = goals;
        this.activeGoals = activeGoals;
        this.completedGoals = completedGoals;
        this.overdueGoals = overdueGoals;
        this.totalHours = totalHours;
        this.streak = streak;
        this.hoursPerDay = hoursPerDay;
    }

    public List<Goal> getGoals() { return goals; }
    public int getTotalGoals() { return goals.size(); }
    public int getActiveGoals() { return activeGoals; }
    public int getCompletedGoals() { return completedGoals; }
    public int getOverdueGoals() { return overdueGoals; }
    public int getStreak() { return streak; }
    public Map<String, Double> getHoursPerDay() { return hoursPerDay; }

    public String getTotalHoursText() { return String.format("%.1f", totalHours); }

    public int getCompletionRate() {
        return goals.isEmpty() ? 0 : completedGoals * 100 / goals.size();
    }
}
