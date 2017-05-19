package com.financeapp.enitities;

import javax.persistence.*;

/**
 * Created by Matt on 19/05/2017.
 */
@Entity
public class Transaction {

    public Transaction() {
        // Intentionally left blank, required by Hibernate
    }

    public Transaction(String name, String type, float amount, Account account) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.account = account;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String type;

    private float amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private Account account;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
