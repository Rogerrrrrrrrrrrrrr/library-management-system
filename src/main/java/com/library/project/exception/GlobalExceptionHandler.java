package com.library.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + ex.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFound(BookNotFoundException bex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bex.getMessage());
    }
    @ExceptionHandler(BookDeletionException.class)
    public ResponseEntity<String> handleBorrowNotFound(BookDeletionException bex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bex.getMessage());
    }
}
