package com.financeapp.exception;

/**
 * Created by Matt on 16/11/2017.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
