package com.meridian.model;

import java.time.LocalDateTime;

public record TimeEntry(long id, long goalId, String goalTitle,
                        LocalDateTime startTime, LocalDateTime endTime,
                        String notes, long minutes) { }
