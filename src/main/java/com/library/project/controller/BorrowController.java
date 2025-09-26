package com.library.project.controller;

import com.library.project.entity.BorrowRecord;
import com.library.project.entity.User;
import com.library.project.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<BorrowRecord>> getRecordsByUser(@PathVariable Long userId){
    List<BorrowRecord> borrowRecords = borrowService.getBorrowRecordsByUser(userId);
        return ResponseEntity.of(Optional.ofNullable(borrowRecords));
    }

    @GetMapping(value = "/book/{bookId}")
    public ResponseEntity<List<BorrowRecord>> getRecordsByBook(@PathVariable Long bookId){
        List<BorrowRecord> borrowRecords = borrowService.getBorrowRecordsByBook(bookId);
        return ResponseEntity.of(Optional.ofNullable(borrowRecords));
    }

    @PostMapping("/records")
    public ResponseEntity<BorrowRecord> borrowBook(@RequestParam Long userId,@RequestParam Long bookId){
        BorrowRecord borrowRecord = borrowService.borrowBook(userId, bookId);
        return ResponseEntity.status(201).body(borrowRecord);
    }

    @PutMapping("/{recordId}/return")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.returnBook(recordId);
        return ResponseEntity.ok(record);
    }
}
