package com.meridian.model;
public record User(long id, String fullName, String email, String role, String accountStatus) {
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
