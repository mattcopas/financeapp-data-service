package com.financeapp;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Demo app initialization
	 */
	@Bean
	public CommandLineRunner demo(AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {
			List<Transaction> transactionsToAdd = new ArrayList<Transaction>();
			Transaction transaction = new Transaction();
			transaction.setName("Test Transaction");
			transactionsToAdd.add(transaction);

			transactionRepository.save(transactionsToAdd);

			Account accountToAdd = new Account("Test Account 1", "GBP", 100.0F, transactionsToAdd);

			accountRepository.save(accountToAdd);
		};
	}

}
