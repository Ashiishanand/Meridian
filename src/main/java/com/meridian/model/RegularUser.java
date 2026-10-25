package com.meridian.model;

public class RegularUser extends User {

    public RegularUser(int id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash, Role.USER);
    }

    @Override
    public String getDashboardPath() {
        return "/user/dashboard";
    }

    @Override
    public String getRoleLabel() {
        return "User";
    }
}
