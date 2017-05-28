package com.financeapp.services;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.exception.InvalidTransactionTypeException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Matt on 20/05/2017.
 */
@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public boolean performAccountTransaction(Transaction transaction) throws Exception {

        transactionRepository.save(transaction);

        Account accountToUpdate = transaction.getAccount();

        if( ! this.updateAccontBalance(accountToUpdate, transaction, false)) {
            throw new InvalidTransactionTypeException("The transaction type " + transaction.getType() + " is not valid");
        }

        this.addTransactionToAccountTransactionsList(accountToUpdate, transaction);

        accountRepository.save(accountToUpdate);

        return true;
    }

    @Transactional
    public boolean removeAccountTransaction(Transaction transaction) {

        Account accountToRollback = transaction.getAccount();

        this.updateAccontBalance(accountToRollback, transaction, true);

        this.removeTransactionFromTransactionList(accountToRollback, transaction);

        accountRepository.save(accountToRollback);

        transactionRepository.delete(transaction.getId());

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
