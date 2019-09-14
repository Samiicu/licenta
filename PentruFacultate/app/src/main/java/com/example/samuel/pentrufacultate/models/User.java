package com.example.samuel.pentrufacultate.models;

import com.google.gson.Gson;

public class User {

    private String username;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static User fromJson(String jsonUser) {
        return new Gson().fromJson(jsonUser, User.class);
    }
}
