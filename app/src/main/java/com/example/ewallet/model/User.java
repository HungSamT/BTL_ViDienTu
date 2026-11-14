package com.example.ewallet.model;

public class User {
    private int id;
    private String phone;
    private String fullName;
    private String password;
    private String pinCode;
    private long balance;
    private String createdAt;

    public User() {
    }

    public User(int id, String phone, String fullName, String password, String pinCode, long balance, String createdAt) {
        this.id = id;
        this.phone = phone;
        this.fullName = fullName;
        this.password = password;
        this.pinCode = pinCode;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
