package com.financeapp.services;

import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.enitities.User;
import com.financeapp.exception.AccountNotFoundException;
import com.financeapp.exception.EntityDoesNotBelongToUserException;
import com.financeapp.exception.InvalidTransactionTypeException;
import com.financeapp.exception.TransactionNotFoundException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDate;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @Transactional
    public boolean performAccountTransaction(TransactionDTO transactionDTO, Principal principal) throws Exception {

        User user = userRepository.findOneByUsername(principal.getName());

        long accountId = transactionDTO.getAccountId();

        if( ! accountRepository.existsById(accountId)) {

            throw new AccountNotFoundException("Account with id " + accountId + " not found");

        }

        Account account = accountRepository.findOne(accountId);

        if( ! accountService.accountBelongsToUser(account, user)) {
            throw new EntityDoesNotBelongToUserException(
                    "The account with id " + account.getId() + " does not belong to " + principal.getName()
            );
        }

        return this.performAccountTransaction(transactionDTO, account);
    }


    @Transactional
    public boolean performRepeatedTransaction(Transaction transaction, Account account) throws Exception {

        TransactionDTO transactionDTO = new TransactionDTO(
                transaction.getName(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getAccount().getId().intValue()
        );

        if(this.performAccountTransaction(transactionDTO, account)) {
            this.updateLastPerformed(transaction);
            this.updateNextDateToBePerformed(transaction);
        }
        return false;
    }

    @Transactional
    public boolean removeAccountTransaction(Long transactionId, Principal principal) {

        Transaction transaction = transactionRepository.findOne(transactionId);

        if(transaction == null) {
            throw new TransactionNotFoundException(
                    "Transaction with id " + transactionId + " not found"
            );
        }

        Account accountToRollback = transaction.getAccount();

        User user = userRepository.findOneByUsername(principal.getName());

        if( ! accountService.accountBelongsToUser(accountToRollback, user)) {
            throw new EntityDoesNotBelongToUserException(
                    "Transaction on account with id " + accountToRollback.getId() +
                            " does not belong to " + principal.getName()
            );
        }

        this.updateAccountBalance(accountToRollback, transaction, true);

        LOGGER.info("Account " + accountToRollback.getName() + " had it's balance updated successfully");

        this.removeTransactionFromTransactionList(accountToRollback, transaction);

        LOGGER.info("Transaction " + transaction.getName() + " was removed from account " + accountToRollback.getName() + " successfully");

        accountRepository.save(accountToRollback);

        LOGGER.info("Account " + accountToRollback.getName() + " updated successfully");

        transactionRepository.delete(transaction.getId());

        LOGGER.info("Transaction " + transaction.getName() + " deleted successfully");

        return true;
    }

    @Transactional
    private boolean performAccountTransaction(TransactionDTO transactionDTO, Account account) throws Exception {



        Transaction transaction = new Transaction(
                transactionDTO.getName(),
                transactionDTO.getType(),
                transactionDTO.getAmount(),
                account
        );

        Transaction savedTransaction = transactionRepository.save(transaction);

        LOGGER.info("Transaction " + savedTransaction.getId() + " saved successfully");

        if( ! this.updateAccountBalance(account, transaction, false)) {
            InvalidTransactionTypeException invalidTransactionTypeException =
                    new InvalidTransactionTypeException("The transaction type " + transactionDTO.getType() + " is not valid");
            LOGGER.error("Invalid transaction type exception thrown " + invalidTransactionTypeException.getMessage());
            throw invalidTransactionTypeException;
        }

        this.addTransactionToAccountTransactionsList(account, transaction);

        LOGGER.info("Transaction " + transactionDTO.getName() + " added to account " + account.getName() + " successfully");

        accountRepository.save(account);

        LOGGER.info("Account " + account.getName() + " updated successfully");

        return true;
    }


    private boolean updateAccountBalance(Account accountToUpdate, Transaction transaction, boolean rollback) {
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

    private void updateNextDateToBePerformed(Transaction transaction) {

        LocalDate lastPerformed = transaction.getLastPerformed();

        switch (transaction.getRepeatTransactionInterval()) {
            case DAILY:
                transaction.setNextDateToPerformTransaction(lastPerformed.plusDays(1));
                break;
            case WEEKLY:
                transaction.setNextDateToPerformTransaction(lastPerformed.plusWeeks(1));
                break;
            case ANNUAL:
                transaction.setNextDateToPerformTransaction(lastPerformed.plusYears(1));
                break;
        }

        transactionRepository.save(transaction);
    }

    private void updateLastPerformed(Transaction transaction) {
        transaction.setLastPerformed(LocalDate.now());
        transactionRepository.save(transaction);
    }
}
