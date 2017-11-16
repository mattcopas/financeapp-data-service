package com.financeapp.repositories;

import com.financeapp.BaseTest;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.utils.OAuth2TestUtils;
import com.financeapp.utils.RequestTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

/**
 * Created by Matt on 10/06/2017.
 */
public class TransactionRepositoryTest extends BaseTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    private Account account;
    private Transaction transaction;

    private String accessToken;

    private RequestTestUtils requestTestUtils;

    @Before
    public void setup() {
        account = accountRepository.save(new Account());
        transaction = new Transaction("Test Transaction 1", "Income", 100.00F, account);
        transaction = transactionRepository.save(transaction);
        accessToken = OAuth2TestUtils.getAccessToken(restTemplate);
        requestTestUtils = new RequestTestUtils(accessToken, restTemplate);

    }

    @Test
    public void deletingATransactionOverHttpShouldReturn405() throws URISyntaxException {

        ResponseEntity responseEntity = requestTestUtils.sendAuthenticatedRequest(
                null,
                HttpMethod.DELETE,
                "/transactions/" + transaction.getId(),
                String.class
        );

        Assert.assertEquals("Status should be 405", HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

    }

    @Test
    public void deletingATransactionOverHttpWithATransactionBodyShouldReturn404() throws URISyntaxException {

        ResponseEntity responseEntity = requestTestUtils.sendAuthenticatedRequest(
                transaction,
                HttpMethod.DELETE,
                "/transactions",
                String.class
        );

        Assert.assertEquals("Response should be 404",
                HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }
}
