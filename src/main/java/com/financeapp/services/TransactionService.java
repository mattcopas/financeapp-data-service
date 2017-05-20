package com.financeapp.services;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
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
    public boolean performAccountTransaction(Transaction transaction) {

        transactionRepository.save(transaction);

        Account accountToUpdate = transaction.getAccount();
        float newAccountBalance;

        switch(transaction.getType()) {
            case "Income":
                newAccountBalance = accountToUpdate.getBalance() + transaction.getAmount();
                accountToUpdate.setBalance(newAccountBalance);
                accountRepository.save(accountToUpdate);
                break;
            case "Expense":
                newAccountBalance = accountToUpdate.getBalance() - transaction.getAmount();
                accountToUpdate.setBalance(newAccountBalance);
                accountRepository.save(accountToUpdate);
            default:
                return false;
        }

        return true;
    }
}
