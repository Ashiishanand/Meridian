package com.meridian.service;

import com.meridian.dao.GoalDAO;
import com.meridian.dao.TimeEntryDAO;
import com.meridian.model.Goal;
import com.meridian.model.GoalStatus;
import com.meridian.model.ProgressSummary;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgressService {

    private final GoalDAO goalDAO = new GoalDAO();
    private final TimeEntryDAO timeEntryDAO = new TimeEntryDAO();

    public ProgressSummary summarize(int userId) {
        List<Goal> goals = goalDAO.findByUser(userId);

        Map<GoalStatus, Integer> byStatus = new EnumMap<>(GoalStatus.class);
        for (GoalStatus status : GoalStatus.values()) {
            byStatus.put(status, 0);
        }
        for (Goal goal : goals) {
            byStatus.merge(goal.getStatus(), 1, Integer::sum);
        }

        return new ProgressSummary(
                goals,
                byStatus.get(GoalStatus.ACTIVE),
                byStatus.get(GoalStatus.COMPLETED),
                byStatus.get(GoalStatus.OVERDUE),
                timeEntryDAO.totalHours(userId),
                calculateStreak(timeEntryDAO.loggedDates(userId)),
                timeEntryDAO.hoursPerDay(userId, 7));
    }

    private int calculateStreak(Set<LocalDate> dates) {
        LocalDate day = LocalDate.now();
        if (!dates.contains(day)) {
            day = day.minusDays(1);
        }
        int streak = 0;
        while (dates.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }
}
