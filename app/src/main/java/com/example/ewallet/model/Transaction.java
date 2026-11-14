package com.example.ewallet.model;

public class Transaction {
    public static final String TYPE_TOP_UP = "TOP_UP";
    public static final String TYPE_WITHDRAW = "WITHDRAW";
    public static final String TYPE_TRANSFER_OUT = "TRANSFER_OUT";
    public static final String TYPE_TRANSFER_IN = "TRANSFER_IN";

    private long id;
    private int userId;
    private String type;
    private long amount;
    private String counterpartyName;
    private String counterpartyInfo;
    private String note;
    private long balanceAfter;
    private String createdAt;

    public Transaction() {
    }

    public Transaction(long id, int userId, String type, long amount, String counterpartyName,
                       String counterpartyInfo, String note, long balanceAfter, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.counterpartyName = counterpartyName;
        this.counterpartyInfo = counterpartyInfo;
        this.note = note;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getCounterpartyInfo() {
        return counterpartyInfo;
    }

    public void setCounterpartyInfo(String counterpartyInfo) {
        this.counterpartyInfo = counterpartyInfo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
