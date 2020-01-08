package com.reportedsocks.taxiapp.model;

public class User {
    private String name;
    private String email;
    private String id;
    private boolean isDriver;

    public User() {
    }

    public User(String name, String email, String id, boolean isDriver) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.isDriver = isDriver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }
}
