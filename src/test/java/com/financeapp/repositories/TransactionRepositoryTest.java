package com.financeapp.repositories;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Matt on 10/06/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tdd")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @LocalServerPort
    private int port;

    private Account account;
    private Transaction transaction;

    private RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;

    @Before
    public void setup() {
        account = accountRepository.save(new Account());
        transaction = new Transaction("Test Transaction 1", "Income", 100.00F, account);
        transaction = transactionRepository.save(transaction);
        baseUrl = "http://localhost:" + port + "/transactions/";
    }

    @Test(expected = HttpClientErrorException.class)
    public void deletingATransactionOverHttpShouldReturn405() throws URISyntaxException {

        RequestEntity requestEntity = new RequestEntity(
                HttpMethod.DELETE,
                new URI(baseUrl + transaction.getId()));

        try {
            restTemplate.exchange(requestEntity, Object.class);
        } catch (HttpClientErrorException exception) {
            Assert.assertTrue("A 405 exception should be thrown", exception.getMessage().startsWith("405"));
            throw exception;
        }
    }

    @Test(expected = HttpClientErrorException.class)
    public void deletingATransactionOverHttpWithATransactionBodyShouldReturn404() throws URISyntaxException {

        RequestEntity<Transaction> requestEntity = new RequestEntity<>(
                transaction,
                HttpMethod.DELETE,
                new URI(baseUrl)
        );

        try {
            restTemplate.exchange(requestEntity, Object.class);
        } catch(HttpClientErrorException exception) {
            System.out.println(exception.getMessage());
            Assert.assertTrue("A 404 exception should be thrown", exception.getMessage().startsWith("404"));
            throw exception;
        }
    }
}
