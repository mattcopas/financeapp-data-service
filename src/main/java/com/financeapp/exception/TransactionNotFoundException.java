package com.financeapp.exception;

/**
 * Created by Matt on 16/11/2017.
 */
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
