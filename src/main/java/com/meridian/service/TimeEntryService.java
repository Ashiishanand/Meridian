package com.meridian.service;

import com.meridian.dao.TimeEntryDAO;
import com.meridian.exception.InvalidTimeEntryException;
import com.meridian.util.ValidationUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class TimeEntryService {

    private final TimeEntryDAO timeEntryDAO = new TimeEntryDAO();

    public boolean logTime(int userId, String goalText, String startText, String endText, String notesText)
            throws InvalidTimeEntryException {
        int goalId = ValidationUtil.parseId(goalText);
        if (goalId <= 0) {
            throw new InvalidTimeEntryException("Please choose a goal.");
        }

        LocalDateTime start = parseTime(startText, "start");
        LocalDateTime end = parseTime(endText, "end");
        if (!end.isAfter(start)) {
            throw new InvalidTimeEntryException("End time must be after the start time.");
        }
        if (end.isAfter(LocalDateTime.now())) {
            throw new InvalidTimeEntryException("End time cannot be in the future.");
        }
        if (Duration.between(start, end).toHours() >= 24) {
            throw new InvalidTimeEntryException("A single entry must be shorter than 24 hours.");
        }

        String notes = notesText == null ? "" : notesText.trim();
        if (notes.length() > 255) {
            throw new InvalidTimeEntryException("Notes must be at most 255 characters.");
        }
        return timeEntryDAO.log(userId, goalId, start, end, notes);
    }

    private LocalDateTime parseTime(String text, String label) throws InvalidTimeEntryException {
        if (text == null || text.isBlank()) {
            throw new InvalidTimeEntryException("Please enter the " + label + " time.");
        }
        try {
            return LocalDateTime.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new InvalidTimeEntryException("The " + label + " time is not valid.");
        }
    }
}
