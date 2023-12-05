package com.example.livraison.model;

public class User {
    private String userId;
    private String address;
    private String phoneNumber;
    private String role;

    public User() {
    }

    public User(String userId, String address, String phoneNumber, String role) {
        this.userId = userId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
