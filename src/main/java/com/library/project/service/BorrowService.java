package com.library.project.service;

import com.library.project.entity.Book;
import com.library.project.entity.BorrowRecord;
import com.library.project.entity.User;
import com.library.project.exception.BookNotFoundException;
import com.library.project.exception.DuplicateBorrowException;
import com.library.project.exception.InvalidReturnException;
import com.library.project.exception.UserNotFoundException;
import com.library.project.repository.BookRepository;
import com.library.project.repository.BorrowRepository;
import com.library.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class BorrowService {
    private static final Logger log = Logger.getLogger(BorrowService.class.getName());
    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        log.info("Borrow request received for userId={} and bookId={} " + userId +"  " + bookId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
        //unnecessary DB calls
//        if (borrowRepository.existsByBook_BookIdAndStatus(bookId, BorrowRecord.Status.BORROWED)) {
//            throw new RuntimeException("Book is already borrowed");
//        }
        //removing due to bug in case qty is 5 and status is issued
//        if (book.getStatus() == Book.Status.ISSUED) {
//            throw new RuntimeException("Book is already borrowed");
//        }

        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("No copies available to borrow");
        }
        if (book.isDeleted()) {
            throw new IllegalStateException("Cannot borrow a deleted book");
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
        log.info("Updating book status and quantity for bookId={} " + bookId);
        bookRepository.save(book);

        log.info("Borrow record created for userId={} and bookId={} " + userId +" " + bookId);
        return borrowRepository.save(borrowRecord);
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        log.info("Return request received for borrowRecordId={} " + recordId);

        BorrowRecord borrowRecord = borrowRepository.findById(recordId)
                .orElseThrow(() -> new IllegalStateException("Borrow record not found"));

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

        log.info("Updating book status and quantity for returned bookId={} " + book.getBookId());
        bookRepository.save(book);

        log.info("Borrow record updated as returned for recordId={} " + recordId);
        return borrowRepository.save(borrowRecord);
    }

    public List<BorrowRecord> getBorrowRecordsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<BorrowRecord> records = borrowRepository.findByUser_UserId(userId);

        if (records.isEmpty()) {
            throw new RuntimeException("No borrow records found for user with id: " + userId);
        }
        return records;
    }

    public List<BorrowRecord> getBorrowRecordsByBook(Long bookId) {

        bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + bookId));

        List<BorrowRecord> records = borrowRepository.findByBook_BookId(bookId);

        if (records.isEmpty()) {
            throw new RuntimeException("No borrow records found for book with id: " + bookId);
        }

        return records;
    }
}
