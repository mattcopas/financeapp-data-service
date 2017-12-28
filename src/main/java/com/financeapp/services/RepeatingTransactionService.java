package com.financeapp.services;

import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Matt on 17/11/2017.
 */
@Component
@EnableScheduling
public class RepeatingTransactionService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    @Scheduled(fixedRate = 60 * 1000)
    public void checkRepeatedTransactionsHaveBeenPerformed() throws Exception {

        LOGGER.info("Checking outstanding transactions");

        List<Transaction> repeatingTransactions = transactionRepository.findByRepeatTransactionIntervalNotNull();
        LocalDate currentDate = LocalDate.now();

        for(Transaction transaction : repeatingTransactions) {
            LocalDate dateTransactionIsNextToBePerformed = transaction.getNextDateToPerformTransaction();
            if(currentDate.isAfter(dateTransactionIsNextToBePerformed)) {
                if(transactionService.performRepeatedTransaction(transaction, transaction.getAccount())) {
                    LOGGER.info(
                                    "Repeated transaction " + transaction.getName() + " " +
                                    "performed on account " + transaction.getAccount().getName()
                    );
                } else {
                    LOGGER.error(
                            "Repeated transaction " + transaction.getName() + " " +
                                    "failed for account " + transaction.getAccount().getName()
                    );
                }
            }
        }
    }
}
