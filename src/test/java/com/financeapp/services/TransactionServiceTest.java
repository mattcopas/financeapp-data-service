package com.financeapp.services;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.exception.InvalidTransactionTypeException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Matt on 20/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("tdd")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account accountToTest;
    private Long accountId;

    @Before
    public void setup() {
        accountToTest = accountRepository.save(new Account("Test Account", "Current", "GBP", 100.00F));
        accountId = accountToTest.getId();
    }

    @Test
    public void performingTransactionShouldIncreaseAccountBalanceForIncomeTransactions() throws Exception {

        Transaction transaction = new Transaction("Test Income Transaction", "Income", 50.00F, accountToTest);

        transactionService.performAccountTransaction(transaction);

        Account updatedAccount = accountRepository.findOne(accountId);

        Assert.assertEquals("Balance should be increased to 150.00", 150.00F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void performingTransactionShouldAddToTheAccountsTransactionList() throws Exception {
        Transaction transaction = new Transaction("Test Transaction Added to Account", "Expense", 10.00F, accountToTest);

        transactionService.performAccountTransaction(transaction);

        Assert.assertEquals("The account transaction list should have 1 item", 1, accountRepository.findOne(accountId).getTransactionList().size());
    }

    @Test
    public void performingTransactionShouldDecreaseAccountBalanceForExpensesTransactions() throws Exception {

        Transaction transaction = new Transaction("Test Expense Transaction", "Expense", 50.00F, accountToTest);

        transactionService.performAccountTransaction(transaction);

        Account updatedAccount = accountRepository.findOne(accountId);

        Assert.assertEquals("Balance should be decreased to 50.00", 50.00F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void performingATransactionShouldSaveTheTransaction() throws Exception {
        Transaction transactionToSave = new Transaction("Test Transaction To Save", "Income", 10.0F, accountToTest);
        transactionService.performAccountTransaction(transactionToSave);

        Assert.assertTrue("The transaction should be saved when performing a transaction",
                transactionRepository.exists(transactionToSave.getId()));
    }

    @Test(expected = InvalidTransactionTypeException.class)
    public void performingATransactionShouldReturnFalseIfAnInvalidTransactionTypeIsUsed() throws Exception {
        Transaction invalidTransaction = new Transaction("Invalid Transaction", "Invalid type", 10.0F, accountToTest);

        Assert.assertFalse(transactionService.performAccountTransaction(invalidTransaction));
    }

    @Test(expected = InvalidTransactionTypeException.class)
    public void performingATransactionWithAnInvalidTransactionTypeShouldNotUpdateTheAccount() throws Exception {
        Transaction invalidTransaction = new Transaction("Invalid Transaction", "Invalid type", 10.0F, accountToTest);
        transactionService.performAccountTransaction(invalidTransaction);

        Assert.assertEquals("The account balance should remain the same", 100.00F, accountRepository.findOne(accountId).getBalance(), 0);
    }
}
