package com.financeapp.controllers;

import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.repositories.AccountRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Matt on 20/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("tdd")
public class TransactionControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testReturns400IfNonExistentAccountIdIsPassed() {

        TransactionDTO transactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, 999);

        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:8081/transaction/add",
                transactionDTO,
                String.class
        );

        Assert.assertEquals("Should return a 400 response code", HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void shouldReturnAcceptedResponseCodeIfAValidAccountIdIsPassed() {
        Account account = accountRepository.save(new Account());
        int id = account.getId().intValue();
        TransactionDTO transactionDTO = new TransactionDTO("Test Transaction", "Income", 100.0F, id);

        ResponseEntity response = restTemplate.postForEntity(
                "http://localhost:8081/transaction/add",
                transactionDTO,
                String.class
        );

        Assert.assertEquals("Should return an Accepted response code", HttpStatus.ACCEPTED, response.getStatusCode());
    }

}
