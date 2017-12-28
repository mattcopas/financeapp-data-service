package com.financeapp.enitities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;
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

    public Account(String name, String type, String currency, BigDecimal initialBalance, User user) {
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = initialBalance;
        this.transactionList = new ArrayList<Transaction>();
        this.user = user;
    }

    @ManyToOne
    @JsonManagedReference
    private User user;

    private String name;

    // TODO Convert to enum
    private String type;

    private String currency;

    private BigDecimal balance;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public User getUser() {
        return user;
    }

}
