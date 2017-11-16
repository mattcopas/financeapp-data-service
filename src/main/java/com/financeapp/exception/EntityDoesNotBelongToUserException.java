package com.financeapp.exception;

/**
 * Created by Matt on 16/11/2017.
 */
public class EntityDoesNotBelongToUserException extends RuntimeException {

    public EntityDoesNotBelongToUserException(String message) {
        super(message);
    }
}
