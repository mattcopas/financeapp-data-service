package com.financeapp.services;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * Created by Matt on 20/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("tdd")
public class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    Account accountToTest;
    Long accountId;

    @Before
    public void setup() {
        accountToTest = new Account("Test Account", "GBP", 100.00F, new ArrayList<>());
        accountRepository.save(accountToTest);
        accountId = accountToTest.getId();
    }

    @Test
    public void performingTransactionShouldIncreaseAccountBalanceForIncomeTransactions() {

        Transaction transaction = new Transaction("Test Income Transaction", "Income", 50.00F, accountToTest);

        transactionService.performAccountTransaction(transaction);

        Account updatedAccount = accountRepository.findOne(accountId);

        Assert.assertEquals("Balance should be increased to 150.00", 150.00F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void performingTransactionShouldIncreaseAccountBalanceForExpensesTransactions() {

        Transaction transaction = new Transaction("Test Expense Transaction", "Expense", 50.00F, accountToTest);

        transactionService.performAccountTransaction(transaction);

        Account updatedAccount = accountRepository.findOne(accountId);

        Assert.assertEquals("Balance should be increased to 150.00", 50.00F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void performingATransactionShouldSaveTheTransaction() {
        Transaction transactionToSave = new Transaction("Test Transaction To Save", "Income", 10.0F, accountToTest);
        transactionService.performAccountTransaction(transactionToSave);

        Assert.assertTrue("The transaction should be saved when performing a transaction",
                transactionRepository.exists(transactionToSave.getId()));
    }

    @Test
    public void performingATransactionShouldReturnFalseIfAnInvalidTransactionTypeIsUsed() {
        Transaction invalidTransaction = new Transaction("Invalid Transaction", "Invalid type", 10.0F, accountToTest);

        Assert.assertFalse(transactionService.performAccountTransaction(invalidTransaction));
    }
}
