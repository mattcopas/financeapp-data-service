package com.financeapp.controllers;

import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Matt on 20/05/2017.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<String> addTransactionUsingAccountId(
            @RequestBody TransactionDTO transactionDTO) {

        Long accountId = Integer.toUnsignedLong(transactionDTO.getAccountId());

        if( ! accountRepository.existsById(accountId)) {

            String responseBody = "Account with id " + accountId + " not found";
            ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            return response;
        }

        Account transactionAccount = accountRepository.findOne(accountId);

        Transaction transaction = new Transaction(
                transactionDTO.getName(),
                transactionDTO.getType(),
                transactionDTO.getAmount(),
                transactionAccount
        );

        if(transactionService.performAccountTransaction(transaction)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>("There was a problem adding the transaction", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
