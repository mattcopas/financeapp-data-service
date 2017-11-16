package com.financeapp.controllers;

import com.financeapp.BaseTest;
import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.enitities.User;
import com.financeapp.exception.AccountNotFoundException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.repositories.UserRepository;
import com.financeapp.services.TransactionService;
import com.financeapp.utils.OAuth2TestUtils;
import com.financeapp.utils.RequestTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.net.URISyntaxException;
import java.security.Principal;

/**
 * Created by Matt on 20/05/2017.
 */
public class TransactionControllerTest extends BaseTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    private Account account;
    private TransactionDTO invalidTransactionDTO;
    private TransactionDTO validTransactionDTO;

    private String accessToken;
    
    private RequestTestUtils requestTestUtils;

    @Before
    public void setup() throws Exception {

        account = accountRepository.save(new Account(
                "Test Account",
                "Current",
                "GBP",
                100.0F,
                userRepository.findOneByUsername("test@test.com"))
        );

        accessToken = OAuth2TestUtils.getAccessToken(restTemplate);
        
        requestTestUtils = new RequestTestUtils(accessToken, restTemplate);

    }

    @Test
    public void testAddingTransactionReturns400IfNonExistentAccountIdIsPassed() throws Exception {

        Mockito.when(this.transactionService.performAccountTransaction(Matchers.any(TransactionDTO.class), Matchers.any(Principal.class))).thenThrow(AccountNotFoundException.class);

        invalidTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, 999);
        
        ResponseEntity response = requestTestUtils.sendAuthenticatedRequest(invalidTransactionDTO, HttpMethod.POST, "/transaction/add", String.class);

        Assert.assertEquals("Should return a 400 response code", HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void addingATransactionShouldReturnAcceptedResponseCodeIfAValidAccountIdIsPassed() throws Exception {

        int id = account.getId().intValue();
        validTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        Mockito.when(this.transactionService.performAccountTransaction(Matchers.any(TransactionDTO.class), Matchers.any(Principal.class))).thenReturn(true);

        ResponseEntity response = requestTestUtils.sendAuthenticatedRequest(
                validTransactionDTO,
                HttpMethod.POST,
                "/transaction/add",
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
                Matchers.any(TransactionDTO.class),
                Matchers.any(Principal.class)
            )
        ).thenReturn(false);

        ResponseEntity response = requestTestUtils.sendAuthenticatedRequest(
                invalidTransactionDTO,
                HttpMethod.POST,
                "/transaction/add",
                String.class
        );

        Assert.assertEquals("The response code should be an internal server error", HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

    }

    @Test
    public void a404ResponseCodeShouldBeReturnedIfANonExistentTransactionIdIsPassedWhenRemovingATransaction() throws URISyntaxException {
        int id = account.getId().intValue();
        validTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        ResponseEntity response = requestTestUtils.sendAuthenticatedRequest(
                validTransactionDTO,
                HttpMethod.POST,
                "/transaction/rollback/999",
                String.class
        );

        Assert.assertEquals("The response code should be a 404 if the transaction is not found",
                HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void shouldReturnA202ResponseCodeIfTheTransactionIsRemovedSuccessfully() throws URISyntaxException {
        int id = account.getId().intValue();
        validTransactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        Mockito.when(
                this.transactionService.removeAccountTransaction(Matchers.any())
        ).thenReturn(true);

        Mockito.when(
                this.transactionRepository.findOne(Matchers.any())
        ).thenReturn(new Transaction());

        ResponseEntity response = requestTestUtils.sendAuthenticatedRequest(
                validTransactionDTO,
                HttpMethod.POST,
                "/transaction/rollback/" + id,
                String.class);

        Assert.assertEquals("Should return a 202 Accepted response code when removing a transaction",
                HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void shouldReturnA500ErrorIfRemovingTheTransactionFails() throws URISyntaxException {
        Mockito.when(
                this.transactionService.removeAccountTransaction(Matchers.any())
        ).thenReturn(false);

        Mockito.when(
                this.transactionRepository.findOne(Matchers.any())
        ).thenReturn(new Transaction());


        ResponseEntity response = requestTestUtils.sendAuthenticatedRequest(
                null,
                HttpMethod.POST,
                "/transaction/rollback/1",
                String.class
        );

        Assert.assertEquals("Should return a 500 error if the transaction fails to rollback",
                HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
