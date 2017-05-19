package com.financeapp.enitities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Matt on 19/05/2017.
 */
@Entity
public class Account {

    public Account() {
        // Intentionally left blank, required by Hibernate
    }

    public Account(String name, String currency, float initialBalance, List<Transaction> transactions) {
        this.name = name;
        this.currency = currency;
        this.balance = initialBalance;
        this.transactionList = transactions;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String currency;

    private float balance;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Transaction> transactionList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
