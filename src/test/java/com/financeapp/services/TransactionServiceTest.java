package com.financeapp.services;

import com.financeapp.BaseTest;
import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.enitities.User;
import com.financeapp.exception.AccountNotFoundException;
import com.financeapp.exception.EntityDoesNotBelongToUserException;
import com.financeapp.exception.InvalidTransactionTypeException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;

/**
 * Created by Matt on 20/05/2017.
 */
public class TransactionServiceTest extends BaseTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account accountToTest;
    private Long accountId;

    private final float INITIAL_ACCOUNT_BALANCE = 100.0F;

    private Principal principal;

    @Before
    public void setup() {
        User savedUser = userRepository.findOneByUsername("test@test.com");
        accountToTest = accountRepository.save(new Account("Test Account", "Current", "GBP", INITIAL_ACCOUNT_BALANCE, savedUser));
        System.out.println("TEST ACCOUNT USERNAME " + accountToTest.getUser().getUsername());
        accountId = accountToTest.getId();

        principal = () -> savedUser.getUsername();
    }

    @Test
    public void performingTransactionShouldIncreaseAccountBalanceForIncomeTransactions() throws Exception {

        TransactionDTO transactionDTO = new TransactionDTO(
                "Test Income Transaction",
                "Income",
                50.00F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionDTO, principal);

        Account updatedAccount = accountRepository.findOne(accountId);

        Assert.assertEquals("Balance should be increased to 150.00", 150.00F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void performingTransactionShouldAddToTheAccountsTransactionList() throws Exception {

        TransactionDTO transactionDTO = new TransactionDTO(
                "Test Transaction Added to Account",
                "Expense",
                10.00F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionDTO, principal);

        Assert.assertEquals("The account transaction list should have 1 item", 1, accountRepository.findOne(accountId).getTransactionList().size());
    }

    @Test
    public void performingTransactionShouldDecreaseAccountBalanceForExpensesTransactions() throws Exception {

        TransactionDTO transactionDTO = new TransactionDTO(
                "Test Expense Transaction",
                "Expense",
                50.00F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionDTO, principal);

        Account updatedAccount = accountRepository.findOne(accountId);

        Assert.assertEquals("Balance should be decreased to 50.00", 50.00F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void performingATransactionShouldSaveTheTransaction() throws Exception {

        TransactionDTO transactionToSaveDTO = new TransactionDTO(
                "Test Transaction To Save",
                "Income",
                10.0F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionToSaveDTO, principal);

        Transaction transaction = transactionRepository.findOneByName("Test Transaction To Save");

        Assert.assertTrue("The transaction should be saved when performing a transaction",
                transactionRepository.exists(transaction.getId()));
    }

    @Test(expected = InvalidTransactionTypeException.class)
    public void performingATransactionShouldReturnFalseIfAnInvalidTransactionTypeIsUsed() throws Exception {

        TransactionDTO invalidTransactionDTO = new TransactionDTO(
                "Invalid Transaction",
                "Invalid type",
                10.0F,
                accountToTest.getId().intValue()
        );

        Assert.assertFalse(transactionService.performAccountTransaction(invalidTransactionDTO, principal));
    }

    @Test(expected = InvalidTransactionTypeException.class)
    public void performingATransactionWithAnInvalidTransactionTypeShouldNotUpdateTheAccount() throws Exception {

        TransactionDTO invalidTransactionDTO = new TransactionDTO(
                "Invalid Transaction",
                "Invalid type",
                10.0F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(invalidTransactionDTO, principal);

        Assert.assertEquals("The account balance should remain the same", 100.00F, accountRepository.findOne(accountId).getBalance(), 0);
    }

    @Test
    public void removingAnIncomeTransactionShouldRemoveTheTransactionFromTheDatabase() throws Exception {

        TransactionDTO transactionToRemoveDTO = new TransactionDTO(
                "Test Transaction To Remove",
                "Income",
                50.0F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionToRemoveDTO, principal);

        Transaction transactionToRemove = transactionRepository.findOneByName("Test Transaction To Remove");

        transactionService.removeAccountTransaction(transactionToRemove);

        Assert.assertEquals("The transaction list size should now be 0",
                0, accountRepository.findOne(accountId).getTransactionList().size());
    }

    @Test
    public void rollingBackAnIncomeTransactionShouldDecreaseTheAccountBalance() throws Exception {

        TransactionDTO transactionToRemoveDTO = new TransactionDTO(
                "Test Transaction To Remove",
                "Income",
                50.0F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionToRemoveDTO, principal);

        Transaction transactionToRemove = transactionRepository.findOneByName("Test Transaction To Remove");

        transactionService.removeAccountTransaction(transactionToRemove);

        Account updatedAccount = accountRepository.findOne(accountToTest.getId());

        Assert.assertEquals("The account balance should decrease to 100.0F", 100.0F, updatedAccount.getBalance(), 0);
    }

    @Test
    public void rollingBackAnExpenseTransactionShouldDecreaseTheAccountBalance() throws Exception {

        TransactionDTO transactionToRemoveDTO = new TransactionDTO(
                "Test Transaction To Remove",
                "Expense",
                50.0F,
                accountToTest.getId().intValue()
        );

        transactionService.performAccountTransaction(transactionToRemoveDTO, principal);

        Transaction transactionToRemove = transactionRepository.findOneByName("Test Transaction To Remove");

        transactionService.removeAccountTransaction(transactionToRemove);

        Account updatedAccount = accountRepository.findOne(accountToTest.getId());

        Assert.assertEquals("The account balance should decrease to 100.0F", 100.0F, updatedAccount.getBalance(), 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testAccountNotFoundExceptionIsThrownIfANonExistentAccountIsProvided() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, 999);
        transactionService.performAccountTransaction(transactionDTO, principal);
    }

    @Test(expected = EntityDoesNotBelongToUserException.class)
    public void testEntityDoesNotBelongToUserExceptionIsThrown() throws Exception {

        TransactionDTO transactionDTO = new TransactionDTO(
                "Test Transaction",
                "Income",
                100.0F,
                accountToTest.getId().intValue()
        );

        Principal wrongPrincipal = () -> "wronguser@test.com";

        transactionService.performAccountTransaction(transactionDTO, wrongPrincipal);
    }
}
