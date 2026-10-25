package com.meridian.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class GoalTest {

    private Goal goal(double target, double logged, LocalDate deadline, GoalStatus status) {
        Goal goal = new Goal();
        goal.setTargetHours(target);
        goal.setLoggedHours(logged);
        goal.setDeadline(deadline);
        goal.setStatus(status);
        return goal;
    }

    @Test
    void progressIsCalculatedAsPercentage() {
        Goal goal = goal(10, 2.5, LocalDate.now().plusDays(5), GoalStatus.ACTIVE);
        assertEquals(25, goal.getProgressPercent());
    }

    @Test
    void progressIsCappedAtHundred() {
        Goal goal = goal(2, 5, LocalDate.now().plusDays(5), GoalStatus.COMPLETED);
        assertEquals(100, goal.getProgressPercent());
    }

    @Test
    void zeroTargetGivesZeroProgress() {
        Goal goal = goal(0, 3, LocalDate.now().plusDays(5), GoalStatus.ACTIVE);
        assertEquals(0, goal.getProgressPercent());
    }

    @Test
    void deadlineNoteForUpcomingGoal() {
        Goal goal = goal(10, 1, LocalDate.now().plusDays(3), GoalStatus.ACTIVE);
        assertEquals("3 days left", goal.getDeadlineNote());
    }

    @Test
    void deadlineNoteForGoalDueToday() {
        Goal goal = goal(10, 1, LocalDate.now(), GoalStatus.ACTIVE);
        assertEquals("Due today", goal.getDeadlineNote());
    }

    @Test
    void deadlineNoteForOverdueGoal() {
        Goal goal = goal(10, 1, LocalDate.now().minusDays(1), GoalStatus.OVERDUE);
        assertEquals("Overdue by 1 day", goal.getDeadlineNote());
    }

    @Test
    void deadlineNoteForCompletedGoal() {
        Goal goal = goal(10, 10, LocalDate.now().minusDays(4), GoalStatus.COMPLETED);
        assertEquals("Completed", goal.getDeadlineNote());
    }
}
