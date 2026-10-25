package com.meridian.service;

import com.meridian.dao.GoalDAO;
import com.meridian.dao.GoalTypeDAO;
import com.meridian.exception.InvalidGoalException;
import com.meridian.util.ValidationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class GoalService {

    private final GoalDAO goalDAO = new GoalDAO();
    private final GoalTypeDAO goalTypeDAO = new GoalTypeDAO();

    public void addGoal(int userId, String typeText, String nameText, String targetText, String deadlineText)
            throws InvalidGoalException {
        String name = nameText == null ? "" : nameText.trim();
        if (name.length() < 3 || name.length() > 150) {
            throw new InvalidGoalException("Goal name must be 3 to 150 characters.");
        }

        int typeId = ValidationUtil.parseId(typeText);
        boolean validType = goalTypeDAO.findActive().stream().anyMatch(t -> t.getId() == typeId);
        if (!validType) {
            throw new InvalidGoalException("Please choose a valid goal type.");
        }

        double target = parseTarget(targetText);
        LocalDate deadline = parseDeadline(deadlineText);
        goalDAO.create(userId, typeId, name, target, deadline);
    }

    private double parseTarget(String text) throws InvalidGoalException {
        String message = "Target must be between 0.5 and 1000 hours.";
        if (text == null) {
            throw new InvalidGoalException(message);
        }
        double value;
        try {
            value = Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidGoalException(message);
        }
        if (Double.isNaN(value) || value < 0.5 || value > 1000) {
            throw new InvalidGoalException(message);
        }
        return value;
    }

    private LocalDate parseDeadline(String text) throws InvalidGoalException {
        if (text == null) {
            throw new InvalidGoalException("Please choose a deadline.");
        }
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new InvalidGoalException("Please choose a valid deadline.");
        }
        if (deadline.isBefore(LocalDate.now())) {
            throw new InvalidGoalException("Deadline cannot be in the past.");
        }
        return deadline;
    }
}
