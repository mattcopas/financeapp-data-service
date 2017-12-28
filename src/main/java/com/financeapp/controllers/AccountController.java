package com.financeapp.controllers;

import com.financeapp.DTOs.AccountDTO;
import com.financeapp.enitities.Account;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Created by user1 on 12/12/2017.
 */
@RestController
public class AccountController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("/account/add")
    public ResponseEntity addAccount(@RequestBody AccountDTO accountDTO) {

        Account account = new Account(
                accountDTO.getName(),
                accountDTO.getType(),
                accountDTO.getCurrency(),
                new BigDecimal(accountDTO.getInitialBalance()),
                userRepository.findOne(accountDTO.getUserId())
        );

        Account savedAccount = accountRepository.save(account);

        return new ResponseEntity<String>("Account " + savedAccount.getName() + " saved", HttpStatus.ACCEPTED);
    }
}
