package com.meridian.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class User implements Serializable {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private final Role role;
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    protected User(int id, String name, String email, String passwordHash, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public abstract String getDashboardPath();

    public abstract String getRoleLabel();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public String getCreatedAtText() {
        return createdAt == null ? "-" : createdAt.format(DISPLAY);
    }

    public String getLastLoginText() {
        return lastLogin == null ? "Never" : lastLogin.format(DISPLAY);
    }

    @Override
    public String toString() {
        return getRoleLabel() + "[" + id + ", " + email + "]";
    }
}
