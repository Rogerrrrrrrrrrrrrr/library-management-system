package com.library.project.exception;

public class DuplicateBorrowException extends RuntimeException {

    public DuplicateBorrowException(String message) {
        super(message);
    }
}
