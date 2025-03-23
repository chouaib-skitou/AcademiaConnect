package com.academiaconnect.auth.authservice.application.exception;

public class UnverifiedAccountException extends RuntimeException {
    public UnverifiedAccountException(String message) {
        super(message);
    }
}