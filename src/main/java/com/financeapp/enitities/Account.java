package com.financeapp.enitities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 19/05/2017.
 */
@Entity
public class Account extends BaseEntity {

    public Account() {
        // Intentionally left blank, required by Hibernate
    }

    public Account(String name, String type, String currency, float initialBalance) {
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = initialBalance;
        this.transactionList = new ArrayList<Transaction>();
    }

    private String name;

    private String type;

    private String currency;

    private float balance;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Transaction> transactionList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }
}