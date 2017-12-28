package com.financeapp.services;

import com.financeapp.BaseTest;
import com.financeapp.enitities.Account;
import com.financeapp.enitities.User;
import com.financeapp.repositories.AccountRepository;
import com.financeapp.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Created by Matt on 16/11/2017.
 */
public class AccountServiceTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    @Test
    public void testAccountBelongsToUserReturnsTrueIfAccountBelongsToUser() {
        User user = userRepository.save(new User("test2@test.com", "password"));
        Account account = accountRepository.save(new Account("Test Account", "Current", "GBP", new BigDecimal("0"), user));
        boolean accountBelongsToUser = accountService.accountBelongsToUser(account, user);

        Assert.assertEquals("Should return true",
                true, accountBelongsToUser);
    }

    @Test
    public void testAccountBelongsToUserReturnsFalseIfAccountDoesaNotBelongToUser() {
        User user = userRepository.save(new User("test2@test.com", "password"));
        Account account = accountRepository.save(new Account("Test Account", "Current", "GBP", new BigDecimal("0"), user));
        boolean accountBelongsToUser = accountService.accountBelongsToUser(account, new User());

        Assert.assertEquals("Should return true",
                false, accountBelongsToUser);
    }
}
