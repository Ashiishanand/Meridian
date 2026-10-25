package com.meridian.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.meridian.exception.InvalidGoalException;
import org.junit.jupiter.api.Test;

class GoalServiceTest {

    private final GoalService service = new GoalService();

    @Test
    void goalNameCannotBeTooShort() {
        InvalidGoalException e = assertThrows(InvalidGoalException.class,
                () -> service.addGoal(1, "1", "ab", "5", "2030-01-01"));
        assertEquals("Goal name must be 3 to 150 characters.", e.getMessage());
    }

    @Test
    void goalNameCannotBeMissing() {
        InvalidGoalException e = assertThrows(InvalidGoalException.class,
                () -> service.addGoal(1, "1", null, "5", "2030-01-01"));
        assertEquals("Goal name must be 3 to 150 characters.", e.getMessage());
    }
}
