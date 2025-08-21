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

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    //used to create records
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        //while creating records, we have to change status,issued date and user details
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User Not Found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new RuntimeException("Book Not F0und"));

        // Check availability
        if (borrowRepository.existsByBook_BookIdAndStatus(bookId, BorrowRecord.Status.BORROWED)) {
            throw new BookNotFoundException("Book is already borrowed");
        }

        // Check duplicate borrow by same user
        if (borrowRepository.existsByUser_UserIdAndBook_BookIdAndStatus(userId, bookId, BorrowRecord.Status.BORROWED)) {
            throw new DuplicateBorrowException("User already borrowed this book");
        }

        BorrowRecord borrowRecord = new BorrowRecord();

        borrowRecord.setUser(user);
        borrowRecord.setBook(book);
        borrowRecord.setIssuedDate(new Date());
        borrowRecord.setStatus(BorrowRecord.Status.BORROWED);

        //update status
        book.setStatus(Book.Status.ISSUED.ISSUED);
        bookRepository.save(book);

        return borrowRepository.save(borrowRecord);
    }

    @Transactional
  public BorrowRecord returnBook(Long recordId){
        //whenever a book is returned, its returned date should be there and its status should be updated
      BorrowRecord borrowRecord = borrowRepository.findById(recordId)
              .orElseThrow(()-> new RuntimeException("Borrow record not found"));

        if (borrowRecord.getStatus() != BorrowRecord.Status.BORROWED) {
            throw new InvalidReturnException("Book is already returned");
        }

      borrowRecord.setReturnDate(new Date());
      borrowRecord.setStatus(BorrowRecord.Status.RETURNED);

        Book book = borrowRecord.getBook();
        book.setStatus(Book.Status.AVAILABLE);
        bookRepository.save(book);
      return borrowRepository.save(borrowRecord);
    }

    public List<BorrowRecord> getBorrowRecordsByUser(Long userId){
        //since no method,hence using custom method
        //we have to get records of all user
        return borrowRepository.findByUser_UserId(userId);
    }

    public List<BorrowRecord> getBorrowRecordsByBook(Long bookId){
    return borrowRepository.findByBook_BookId(bookId);
    }
}
