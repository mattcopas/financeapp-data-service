package com.financeapp.controllers;

import com.financeapp.DTOs.TransactionDTO;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.Transaction;
import com.financeapp.exception.AccountNotFoundException;
import com.financeapp.exception.EntityDoesNotBelongToUserException;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.TransactionRepository;
import com.financeapp.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by Matt on 20/05/2017.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<String> addTransactionUsingAccountId(
            @RequestBody TransactionDTO transactionDTO,
            Principal principal
    ) throws Exception {

        try {
            boolean transactionResult = transactionService.performAccountTransaction(transactionDTO, principal);
            if(transactionResult) {
                return new ResponseEntity<String>("Transaction added successfully", HttpStatus.ACCEPTED);
            }
        } catch (AccountNotFoundException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityDoesNotBelongToUserException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

        LOGGER.error("Internal server error when adding transaction " + transactionDTO.getName());
        return new ResponseEntity<>("There was a problem adding the transaction", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @RequestMapping(value = "/rollback/{transactionId}", method = RequestMethod.POST)
    public ResponseEntity<String> rollbackTransactionByTransactionId(@PathVariable int transactionId) {

        // TODO Refactor into TransactionService
        Transaction transaction = transactionRepository.findOne((long) transactionId);

        if(transaction == null) {
            LOGGER.error("Transaction not found with id " + transactionId);
            return new ResponseEntity<>("The transaction with id " + transactionId + " was not found", HttpStatus.NOT_FOUND);
        }

        if(transactionService.removeAccountTransaction(transactionRepository.findOne((long) transactionId))) {
            LOGGER.info("Transaction " + transactionId + " rolled back successfully");
            return new ResponseEntity<>("Transaction removed", HttpStatus.ACCEPTED);
        }

        LOGGER.error("Internal server error when rolling back transaction with id " + transactionId);
        return new ResponseEntity<>("An error occurred when removing the transaction " + transactionId, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
