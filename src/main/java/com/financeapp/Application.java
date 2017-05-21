package com.financeapp;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.services.TransactionService;
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
	public CommandLineRunner demo(
			AccountRepository accountRepository,
			TransactionRepository transactionRepository,
			TransactionService transactionService) {

		return (args) -> {

			Account accountToAdd = new Account("Test Account 1", "Current", "GBP", 100.0F, new ArrayList<Transaction>());

			accountRepository.save(accountToAdd);

			List<Transaction> transactionsToAdd = new ArrayList<Transaction>();
			Transaction transaction = new Transaction("Test Transaction 1", "Income", 25.0F, accountToAdd);
			transactionService.performAccountTransaction(transaction);
		};

	}

}
