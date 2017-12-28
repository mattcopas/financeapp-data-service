package com.financeapp.enitities;

import com.financeapp.enums.RepeatTransactionInterval;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Matt on 19/05/2017.
 */
@Entity
public class Transaction extends BaseEntity {

    public Transaction() {
        // Intentionally left blank, required by Hibernate
    }

    public Transaction(String name, String type, BigDecimal amount, Account account) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.repeatTransactionInterval = null;
        this.nextDateToPerformTransaction = null;
        this.lastPerformed = null;
    }

    public Transaction(
            String name,
            String type,
            BigDecimal amount,
            Account account,
            RepeatTransactionInterval repeatTransactionInterval
    ) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.repeatTransactionInterval = repeatTransactionInterval;

        switch (repeatTransactionInterval) {
            case DAILY:
                this.nextDateToPerformTransaction = LocalDate.now().plusDays(1);
                break;
            case WEEKLY:
                this.nextDateToPerformTransaction = LocalDate.now().plusDays(7);
                break;
            case MONTHLY:
                this.nextDateToPerformTransaction = LocalDate.now().plusMonths(1);
                break;
            case ANNUAL:
                this.nextDateToPerformTransaction = LocalDate.now().plusYears(1);
                break;
        }
    }

    private String name;

    // TODO Convert to enum
    private String type;

    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private Account account;

    private RepeatTransactionInterval repeatTransactionInterval;
    private LocalDate nextDateToPerformTransaction;
    private LocalDate lastPerformed;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public RepeatTransactionInterval getRepeatTransactionInterval() {
        return this.repeatTransactionInterval;
    }

    public void setRepeatTransactionInterval(RepeatTransactionInterval repeatTransactionInterval) {
        this.repeatTransactionInterval = repeatTransactionInterval;
    }

    public LocalDate getNextDateToPerformTransaction() {
        return nextDateToPerformTransaction;
    }

    public void setNextDateToPerformTransaction(LocalDate nextDateToPerformTransaction) {
        this.nextDateToPerformTransaction = nextDateToPerformTransaction;
    }

    public LocalDate getLastPerformed() {
        return lastPerformed;
    }

    public void setLastPerformed(LocalDate lastPerformed) {
        this.lastPerformed = lastPerformed;
    }
}
