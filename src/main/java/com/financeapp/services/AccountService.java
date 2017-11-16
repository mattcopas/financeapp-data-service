package com.financeapp.services;

import com.financeapp.enitities.Account;
import com.financeapp.enitities.User;
import org.springframework.stereotype.Service;

/**
 * Created by Matt on 16/11/2017.
 */
@Service
public class AccountService {

    public boolean accountBelongsToUser(Account account, User user) {
        return account.getUser().equals(user);
    }
}
