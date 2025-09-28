package com.library.project.service;

import com.library.project.dto.BorrowRequestDTO;
import com.library.project.dto.BorrowResponseDTO;
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

    public BorrowResponseDTO toBorrowResponseDTO(BorrowRecord record) {
        BorrowResponseDTO dto = new BorrowResponseDTO();
        dto.setRecordId(record.getRecordId());
        dto.setUserId(record.getUser().getUserId());
        dto.setUserName(record.getUser().getName());
        dto.setBookId(record.getBook().getBookId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setIssuedDate(record.getIssuedDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setStatus(record.getStatus() != null ? record.getStatus().name() : null);
        return dto;
    }

    public BorrowRecord fromBorrowRequestDTO(BorrowRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("No copies available to borrow");
        }
        if (book.isDeleted()) {
            throw new IllegalStateException("Cannot borrow a deleted book");
        }

        if (borrowRepository.existsByUser_UserIdAndBook_BookIdAndStatus(user.getUserId(), book.getBookId(), BorrowRecord.Status.BORROWED)) {
            throw new DuplicateBorrowException("User already borrowed this book");
        }

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setIssuedDate(new Date());
        borrowRecord.setStatus(BorrowRecord.Status.BORROWED);

        // Update book quantity and status
        book.setQuantity(book.getQuantity() - 1);
        book.setStatus(book.getQuantity() == 0 ? Book.Status.ISSUED : Book.Status.AVAILABLE);
        bookRepository.save(book);

        return borrowRecord;
    }


        @Transactional
        public BorrowRecord borrowBook(BorrowRequestDTO request) {
            log.info("Borrow request received for userId=" + request.getUserId() + ", bookId=" + request.getBookId());
            BorrowRecord record = fromBorrowRequestDTO(request);
            log.info("Borrow record created for userId=" + request.getUserId() + ", bookId=" + request.getBookId());
        //unnecessary DB calls
//        if (borrowRepository.existsByBook_BookIdAndStatus(bookId, BorrowRecord.Status.BORROWED)) {
//            throw new RuntimeException("Book is already borrowed");
//        }
        //removing due to bug in case qty is 5 and status is issued
//        if (book.getStatus() == Book.Status.ISSUED) {
//            throw new RuntimeException("Book is already borrowed");
//        }






////use builder pattern
//        return borrowRepository.save(borrowRecord);
//        BorrowRecord borrowRecord = BorrowRecord.builder()
//                .user(user)
//                .book(book)
//                .issuedDate(new Date())
//                .status(BorrowRecord.Status.BORROWED)
//                .build();


        return borrowRepository.save(record);
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
