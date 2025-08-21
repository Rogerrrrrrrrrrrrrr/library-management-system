package com.library.project.controller;

import com.library.project.entity.BorrowRecord;
import com.library.project.entity.User;
import com.library.project.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping(value = "/api/user/{userId}")
    public ResponseEntity<List<BorrowRecord>> getRecordsByUser(@PathVariable Long userId){
    List<BorrowRecord> borrowRecords = borrowService.getBorrowRecordsByUser(userId);
    return new ResponseEntity<>(borrowRecords, HttpStatus.OK);
    }

    @GetMapping(value = "/api/book/{bookId}")
    public ResponseEntity<List<BorrowRecord>> getRecordsByBook(@PathVariable Long bookId){
        List<BorrowRecord> borrowRecords = borrowService.getBorrowRecordsByBook(bookId);
        return new ResponseEntity<>(borrowRecords,HttpStatus.OK);
    }

    @PostMapping("/api/records")
    public ResponseEntity<BorrowRecord> borrowBook(@RequestParam Long userId,@RequestParam Long bookId){
        BorrowRecord borrowRecord = borrowService.borrowBook(userId, bookId);
        return new ResponseEntity<>(borrowRecord,HttpStatus.CREATED);
    }

    @PutMapping("/api/{recordId}/return")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.returnBook(recordId);
        return new ResponseEntity<>(record, HttpStatus.OK);
    }
}
