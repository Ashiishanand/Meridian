package com.meridian.exception;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String email) {
        super("An account with email '" + email + "' already exists.");
    }
}
