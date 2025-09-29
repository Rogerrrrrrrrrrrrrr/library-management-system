package com.library.project.controller;

import com.library.project.dto.BorrowRequestDTO;
import com.library.project.dto.BorrowResponseDTO;
import com.library.project.entity.BorrowRecord;
import com.library.project.entity.User;
import com.library.project.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<BorrowResponseDTO>> getRecordsByUser(@PathVariable Long userId){
        List<BorrowRecord> records = borrowService.getBorrowRecordsByUser(userId);
        List<BorrowResponseDTO> responseList = new ArrayList<>();

        for (BorrowRecord record : records) {
            responseList.add(borrowService.toBorrowResponseDTO(record));
        }

        if (responseList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping(value = "/book/{bookId}")
    public ResponseEntity<List<BorrowResponseDTO>> getRecordsByBook(@PathVariable Long bookId) {
        List<BorrowRecord> records = borrowService.getBorrowRecordsByBook(bookId);
        List<BorrowResponseDTO> responseList = new ArrayList<>();

        for (BorrowRecord record : records) {
            responseList.add(borrowService.toBorrowResponseDTO(record));
        }

        if (responseList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/borrowed")
    public ResponseEntity<List<BorrowResponseDTO>> getAllBorrowed() {
        List<BorrowRecord> borrowedRecords = borrowService.getAllBorrowedRecords();

        List<BorrowResponseDTO> dtos = borrowedRecords.stream()
                .map(borrowService::toBorrowResponseDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }



    @PostMapping("/borrow")
    public ResponseEntity<BorrowResponseDTO> borrowBook(@RequestBody BorrowRequestDTO request) {
        BorrowRecord borrowRecord = borrowService.borrowBook(request);
        BorrowResponseDTO responseDTO = borrowService.toBorrowResponseDTO(borrowRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @PutMapping("/{recordId}/return")
    public ResponseEntity<BorrowRecord> returnBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.returnBook(recordId);
        return ResponseEntity.ok(record);
    }
}
