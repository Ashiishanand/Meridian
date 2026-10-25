package com.meridian.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.meridian.exception.InvalidTimeEntryException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TimeEntryServiceTest {

    private final TimeEntryService service = new TimeEntryService();

    @Test
    void goalMustBeSelected() {
        InvalidTimeEntryException e = assertThrows(InvalidTimeEntryException.class,
                () -> service.logTime(1, "", "2026-10-20T09:00", "2026-10-20T10:00", ""));
        assertEquals("Please choose a goal.", e.getMessage());
    }

    @Test
    void endMustBeAfterStart() {
        InvalidTimeEntryException e = assertThrows(InvalidTimeEntryException.class,
                () -> service.logTime(1, "3", "2026-10-20T10:00", "2026-10-20T09:00", ""));
        assertEquals("End time must be after the start time.", e.getMessage());
    }

    @Test
    void endCannotBeInTheFuture() {
        String start = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0).toString();
        String end = LocalDateTime.now().plusHours(2).withSecond(0).withNano(0).toString();
        InvalidTimeEntryException e = assertThrows(InvalidTimeEntryException.class,
                () -> service.logTime(1, "3", start, end, ""));
        assertEquals("End time cannot be in the future.", e.getMessage());
    }

    @Test
    void entryMustBeShorterThanADay() {
        InvalidTimeEntryException e = assertThrows(InvalidTimeEntryException.class,
                () -> service.logTime(1, "3", "2026-10-01T08:00", "2026-10-02T09:00", ""));
        assertEquals("A single entry must be shorter than 24 hours.", e.getMessage());
    }

    @Test
    void invalidTimeFormatIsRejected() {
        InvalidTimeEntryException e = assertThrows(InvalidTimeEntryException.class,
                () -> service.logTime(1, "3", "yesterday", "2026-10-20T10:00", ""));
        assertEquals("The start time is not valid.", e.getMessage());
    }
}
