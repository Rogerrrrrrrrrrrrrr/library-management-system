package com.library.project.service;

import com.library.project.entity.Book;
import com.library.project.entity.BorrowRecord;
import com.library.project.entity.User;
import com.library.project.exception.BookNotFoundException;
import com.library.project.exception.DuplicateBorrowException;
import com.library.project.exception.InvalidReturnException;
import com.library.project.repository.BookRepository;
import com.library.project.repository.BorrowRepository;
import com.library.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        //unnecessary DB calls
//        if (borrowRepository.existsByBook_BookIdAndStatus(bookId, BorrowRecord.Status.BORROWED)) {
//            throw new RuntimeException("Book is already borrowed");
//        }
        //removing due to bug in case qty is 5 and status is issued
//        if (book.getStatus() == Book.Status.ISSUED) {
//            throw new RuntimeException("Book is already borrowed");
//        }

        if (book.getQuantity() <= 0) {
            throw new RuntimeException("No copies available to borrow");
        }
        if (book.isDeleted()) {
            throw new RuntimeException("Cannot borrow a deleted book");
        }

        if (borrowRepository.existsByUser_UserIdAndBook_BookIdAndStatus(userId, bookId, BorrowRecord.Status.BORROWED)) {
            throw new DuplicateBorrowException("User already borrowed this book");
        }
        book.setQuantity(book.getQuantity() - 1);

        if (book.getQuantity() == 0) {
            book.setStatus(Book.Status.ISSUED);
        } else {
            book.setStatus(Book.Status.AVAILABLE);
        }

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setIssuedDate(new Date());
        borrowRecord.setStatus(BorrowRecord.Status.BORROWED);




////use builder pattern
//        return borrowRepository.save(borrowRecord);
//        BorrowRecord borrowRecord = BorrowRecord.builder()
//                .user(user)
//                .book(book)
//                .issuedDate(new Date())
//                .status(BorrowRecord.Status.BORROWED)
//                .build();

        book.setStatus(Book.Status.ISSUED);
        bookRepository.save(book);

        return borrowRepository.save(borrowRecord);
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId) {

        BorrowRecord borrowRecord = borrowRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        if (borrowRecord.getStatus() != BorrowRecord.Status.BORROWED) {
            throw new InvalidReturnException("Book is already returned");
        }

        borrowRecord.setReturnDate(new Date());
        borrowRecord.setStatus(BorrowRecord.Status.RETURNED);


        Book book = borrowRecord.getBook();
        if (book == null) {
            throw new BookNotFoundException("Book associated with borrow record not found");
        }

        book.setQuantity(book.getQuantity() + 1);

        if (book.getQuantity() > 0) {
            book.setStatus(Book.Status.AVAILABLE);
        }
        bookRepository.save(book);

        return borrowRepository.save(borrowRecord);
    }

    public List<BorrowRecord> getBorrowRecordsByUser(Long userId) {
        return borrowRepository.findByUser_UserId(userId);
    }

    public List<BorrowRecord> getBorrowRecordsByBook(Long bookId) {
        return borrowRepository.findByBook_BookId(bookId);
    }
}
