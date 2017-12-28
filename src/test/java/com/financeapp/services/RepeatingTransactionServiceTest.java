package com.financeapp.services;

import com.financeapp.BaseTest;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.enitities.User;
import com.financeapp.enums.RepeatTransactionInterval;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Matt on 24/11/2017.
 */
public class RepeatingTransactionServiceTest extends BaseTest {

    @Autowired
    private RepeatingTransactionService repeatingTransactionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @MockBean
    private TransactionService transactionService;
    @Mock
    private Logger logger = LoggerFactory.getLogger(RepeatingTransactionService.class);

    Transaction repeatingTransaction;
    Account accountToTest;
    User user;

    @Before
    public void setup() {

        user = userRepository.save(new User("testuser1", "password"));

        accountToTest = accountRepository.save(new Account(
                "Test Account",
                "Current",
                "GBP",
                new BigDecimal("0"),
                user
        ));

        Transaction transaction = new Transaction(
                "Test Repeating Transaction",
                "Income",
                new BigDecimal("100"),
                accountToTest,
                RepeatTransactionInterval.DAILY
        );

        transaction.setNextDateToPerformTransaction(LocalDate.now().minusDays(1));

        repeatingTransaction = transactionRepository.save(transaction);
    }

    @Test
    public void testTransactionServicePerformRepeatedTransactionIsCalled() throws Exception {

        Mockito.when(
                this.transactionService.performRepeatedTransaction(
                        Matchers.any(Transaction.class), Matchers.any(Account.class)
                )).thenReturn(true);

        repeatingTransactionService.checkRepeatedTransactionsHaveBeenPerformed();

        Mockito.verify(
                transactionService, Mockito.times(1))
                .performRepeatedTransaction(Matchers.any(Transaction.class), Matchers.any(Account.class));
    }

}
