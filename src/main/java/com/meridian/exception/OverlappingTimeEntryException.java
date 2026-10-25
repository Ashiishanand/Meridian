package com.meridian.exception;

public class OverlappingTimeEntryException extends InvalidTimeEntryException {
    public OverlappingTimeEntryException() {
        super("This time range overlaps with another entry you already logged.");
    }
}
