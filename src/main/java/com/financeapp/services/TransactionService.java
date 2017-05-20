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

        if( ! this.updateAccontBalance(accountToUpdate, transaction)) {
            throw new InvalidTransactionTypeException("The transaction type " + transaction.getType() + " is not valid");
        }

        this.addTransactionToAccountTransactionsList(accountToUpdate, transaction);

        accountRepository.save(accountToUpdate);

        return true;
    }

    private boolean updateAccontBalance(Account accountToUpdate, Transaction transaction) {
        float newAccountBalance;

        switch(transaction.getType()) {
            case "Income":
                newAccountBalance = accountToUpdate.getBalance() + transaction.getAmount();
                accountToUpdate.setBalance(newAccountBalance);
                break;
            case "Expense":
                newAccountBalance = accountToUpdate.getBalance() - transaction.getAmount();
                accountToUpdate.setBalance(newAccountBalance);
                break;
            default:
                return false;
        }
        return true;
    }

    public void addTransactionToAccountTransactionsList(Account accountToUpdate, Transaction transaction) {
        accountToUpdate.getTransactionList().add(transaction);
    }
}
