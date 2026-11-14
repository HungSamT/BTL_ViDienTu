package com.example.ewallet.model;

public class LinkedBank {
    private int id;
    private int userId;
    private String bankName;
    private String accountNumber;
    private long mockBalance;

    public LinkedBank() {
    }

    public LinkedBank(int id, int userId, String bankName, String accountNumber, long mockBalance) {
        this.id = id;
        this.userId = userId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.mockBalance = mockBalance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getMockBalance() {
        return mockBalance;
    }

    public void setMockBalance(long mockBalance) {
        this.mockBalance = mockBalance;
    }

    @Override
    public String toString() {
        return bankName + " - " + accountNumber;
    }
}
