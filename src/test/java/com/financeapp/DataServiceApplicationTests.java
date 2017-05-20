package com.financeapp;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.AccountRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("tdd")
public class DataServiceApplicationTests {

	@Autowired
	private AccountRepository acountRepository;

	@Test
	public void testAddingAccountToDatabase() {
		acountRepository.save(new Account("Test Account 1", "GBP", 100.00F, new ArrayList<Transaction>()));

		Account retrievedAccount = acountRepository.findOne(1L);

		Assert.assertEquals("The stored account should have name 'Test Account 1'", "Test Account 1", retrievedAccount.getName());
	}

}
