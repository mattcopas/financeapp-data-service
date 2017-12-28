package com.financeapp.DTOs;

/**
 * Created by Matt on 20/05/2017.
 */
public class TransactionDTO {

    private String name;
    private String type;
    private String rawAmount;
    private int accountId;

    public TransactionDTO () {}

    public TransactionDTO(String name, String type, String rawAmount, int accountId) {
        this.name = name;
        this.type = type;
        this.rawAmount = rawAmount;
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRawAmount() {
        return rawAmount;
    }

    public void setRawAmount(String rawAmount) {
        this.rawAmount = rawAmount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

}
