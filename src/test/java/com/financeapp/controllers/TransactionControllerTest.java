package com.financeapp.controllers;

import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.services.TransactionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Matt on 20/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tdd")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionControllerTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    private Account account;
    private TransactionDTO invalidTransactionDTO;
    private TransactionDTO validTransactionDTO;

    @Before
    public void setup() throws Exception {
        account = accountRepository.save(new Account());
    }

    @Test
    public void testAddingTransactionReturns400IfNonExistentAccountIdIsPassed() {

        invalidTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, 999);

        ResponseEntity response = restTemplate.postForEntity(
                "/transaction/add",
                invalidTransactionDTO,
                String.class
        );

        Assert.assertEquals("Should return a 400 response code", HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void addingATransactionShouldReturnAcceptedResponseCodeIfAValidAccountIdIsPassed() throws Exception {

        int id = account.getId().intValue();
        validTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        Mockito.when(this.transactionService.performAccountTransaction(Matchers.any(Transaction.class))).thenReturn(true);


        ResponseEntity response = restTemplate.postForEntity(
                "/transaction/add",
                validTransactionDTO,
                String.class
        );

        Assert.assertEquals("Should return an Accepted response code", HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void anExceptionShouldBeThrownIfTheAccountTransactionFailsWhenAddingATransaction() throws Exception {
        int id = account.getId().intValue();
        invalidTransactionDTO = new TransactionDTO("Invalid Transaction", "InvalidType", 100.0F, id);

        Mockito.when(
            this.transactionService.performAccountTransaction(
                Matchers.any(Transaction.class)
            )
        ).thenReturn(false);

        ResponseEntity response = restTemplate.postForEntity(
                "/transaction/add",
                invalidTransactionDTO,
                String.class
        );

        Assert.assertEquals("The response code should be an internal server error", HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

    }

    @Test
    public void a404ResponseCodeShouldBeReturnedIfANonExistentTransactionIdIsPassedWhenRemovingATransaction() {
        int id = account.getId().intValue();
        validTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        ResponseEntity removeTransactionResponse = restTemplate.postForEntity(
                "/transaction/rollback/999",
                null,
                String.class
        );

        Assert.assertEquals("The response code should be a 404 if the transaction is not found",
                HttpStatus.NOT_FOUND, removeTransactionResponse.getStatusCode());
    }

    @Test
    public void shouldReturnA202ResponseCodeIfTheTransactionIsRemovedSuccessfully() {
        int id = account.getId().intValue();
        validTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        Mockito.when(
                this.transactionService.removeAccountTransaction(Matchers.any())
        ).thenReturn(true);

        Mockito.when(
                this.transactionRepository.findOne(Matchers.any())
        ).thenReturn(new Transaction());

        ResponseEntity removeTransactionResponse = restTemplate.postForEntity(
                "/transaction/rollback/1",
                null,
                String.class
        );

        Assert.assertEquals("Should return a 202 Accepted response code when removing a transaction",
                HttpStatus.ACCEPTED, removeTransactionResponse.getStatusCode());
    }

    @Test
    public void shouldReturnA500ErrorIfRemovingTheTransactionFails() {
        Mockito.when(
                this.transactionService.removeAccountTransaction(Matchers.any())
        ).thenReturn(false);

        Mockito.when(
                this.transactionRepository.findOne(Matchers.any())
        ).thenReturn(new Transaction());

        ResponseEntity removeTransactionResponse = restTemplate.postForEntity(
                "/transaction/rollback/1",
                null,
                String.class
        );

        Assert.assertEquals("Should return a 500 error if the transaction fails to rollback",
                HttpStatus.INTERNAL_SERVER_ERROR, removeTransactionResponse.getStatusCode());
    }

}
