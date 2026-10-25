package com.meridian.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TimeEntryTest {

    private TimeEntry entry(LocalDateTime start, LocalDateTime end) {
        TimeEntry entry = new TimeEntry();
        entry.setStartTime(start);
        entry.setEndTime(end);
        return entry;
    }

    @Test
    void durationInMinutesOnly() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 20, 9, 0);
        assertEquals("45m", entry(start, start.plusMinutes(45)).getDurationText());
    }

    @Test
    void durationInWholeHours() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 20, 9, 0);
        assertEquals("2h", entry(start, start.plusHours(2)).getDurationText());
    }

    @Test
    void durationInHoursAndMinutes() {
        LocalDateTime start = LocalDateTime.of(2026, 10, 20, 9, 0);
        assertEquals("1h 30m", entry(start, start.plusMinutes(90)).getDurationText());
    }
}
