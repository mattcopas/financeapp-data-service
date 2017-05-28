package com.financeapp.services;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.exception.InvalidTransactionTypeException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Matt on 20/05/2017.
 */
@Service
public class TransactionService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public boolean performAccountTransaction(Transaction transaction) throws Exception {

        transactionRepository.save(transaction);

        LOGGER.info("Transaction " + transaction.getId() + " saved successfully");

        Account accountToUpdate = transaction.getAccount();

        if( ! this.updateAccontBalance(accountToUpdate, transaction, false)) {
            InvalidTransactionTypeException invalidTransactionTypeException =
                    new InvalidTransactionTypeException("The transaction type " + transaction.getType() + " is not valid");
            LOGGER.error("Invalid transaction type exception thrown " + invalidTransactionTypeException.getMessage());
            throw invalidTransactionTypeException;
        }

        this.addTransactionToAccountTransactionsList(accountToUpdate, transaction);

        LOGGER.info("Transaction " + transaction.getName() + " added to account " + accountToUpdate.getName() + " successfully");

        accountRepository.save(accountToUpdate);

        LOGGER.info("Account " + accountToUpdate.getName() + " updated successfully");

        return true;
    }

    @Transactional
    public boolean removeAccountTransaction(Transaction transaction) {

        Account accountToRollback = transaction.getAccount();

        this.updateAccontBalance(accountToRollback, transaction, true);

        LOGGER.info("Account " + accountToRollback.getName() + " had it's balance updated successfully");

        this.removeTransactionFromTransactionList(accountToRollback, transaction);

        LOGGER.info("Transaction " + transaction.getName() + " was removed from account " + accountToRollback.getName() + " successfully");

        accountRepository.save(accountToRollback);

        LOGGER.info("Account " + accountToRollback.getName() + " updated successfully");

        transactionRepository.delete(transaction.getId());

        LOGGER.info("Transaction " + transaction.getName() + " deleted successfully");

        return true;
    }

    private boolean updateAccontBalance(Account accountToUpdate, Transaction transaction, boolean rollback) {
        float newAccountBalance;

        switch(transaction.getType()) {
            case "Income":
                if(rollback) {
                    newAccountBalance = accountToUpdate.getBalance() - transaction.getAmount();
                } else {
                    newAccountBalance = accountToUpdate.getBalance() + transaction.getAmount();
                }
                accountToUpdate.setBalance(newAccountBalance);
                break;
            case "Expense":
                if(rollback) {
                    newAccountBalance = accountToUpdate.getBalance() + transaction.getAmount();
                } else {
                    newAccountBalance = accountToUpdate.getBalance() - transaction.getAmount();
                }
                accountToUpdate.setBalance(newAccountBalance);
                break;
            default:
                return false;
        }
        return true;
    }

    private void addTransactionToAccountTransactionsList(Account accountToUpdate, Transaction transaction) {
        accountToUpdate.getTransactionList().add(transaction);
    }

    private void removeTransactionFromTransactionList(Account accountToRollback, Transaction transaction) {
        accountToRollback.getTransactionList().remove(transaction);
    }
}
