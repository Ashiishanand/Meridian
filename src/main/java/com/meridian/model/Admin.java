package com.meridian.model;

public class Admin extends User {

    public Admin(int id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, Role.ADMIN);
    }

    @Override
    public String getDashboardPath() {
        return "/admin/dashboard";
    }

    @Override
    public String getRoleLabel() {
        return "Admin";
    }
}
