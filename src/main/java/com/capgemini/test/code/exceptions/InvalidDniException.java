package com.capgemini.test.code.exceptions;

public class InvalidDniException extends RuntimeException {
    public InvalidDniException() {
        super("error validation dni");
    }
}