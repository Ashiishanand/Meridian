package com.meridian.model;

public class GoalType {

    private final int id;
    private final String name;
    private final String description;
    private final TrackingMetric trackingMetric;
    private final boolean active;

    public GoalType(int id, String name, String description, TrackingMetric trackingMetric, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.trackingMetric = trackingMetric;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TrackingMetric getTrackingMetric() { return trackingMetric; }
    public boolean isActive() { return active; }
}
