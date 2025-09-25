package com.library.project.service;

import com.library.project.entity.Book;
import com.library.project.exception.BookDeletionException;
import com.library.project.exception.BookNotFoundException;
import com.library.project.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
//keep architrcture / patterns same as Optional Chaining
    @Autowired
    private BookRepository bookRepository;

    public Book createBook(Book book) {
        //add validations before accessing DB
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        if (book.getQuantity() < 0) {
            throw new IllegalArgumentException("Book quantity cannot be negative");
        }
        if (book.getIsbn() != null && bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with ISBN already exists");
        }
        if (book.getStatus() == null) {
            book.setStatus(Book.Status.AVAILABLE);
        }

        return bookRepository.save(book);
    }


    public Book getBookById(Long id){
        return bookRepository.findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book with " + id + " not found"));
    }

    public List<Book> getAllBook(){
        return bookRepository.findAll();
    }

    public Book updateBook(Long id, Book book){
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(()-> new BookNotFoundException("Book with " + id + " not found"));

        if (book.getQuantity()<0){
            throw new IllegalArgumentException("Book quantity cannot be negative");

        }
        if (book.getStatus() == Book.Status.ISSUED && book.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cannot set book status to ISSUED when no copies are available");
        }
    //use builder pattern here
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());

        existingBook.setCategory(book.getCategory());

        //System.out.println("Incoming quantity: " + book.getQuantity());
        existingBook.setQuantity(book.getQuantity());
        //System.out.println("Incoming quantity: " + book.getQuantity());
        existingBook.setStatus(book.getStatus());

        return bookRepository.save(existingBook);
    }


    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with " + id + " not found"));

        if (book.getStatus() == Book.Status.ISSUED) {
            throw new BookDeletionException("Cannot delete a borrowed book");
        }
        book.setDeleted(true);
        bookRepository.save(book);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }


    public Book borrowBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book with " + id + " not found"));

        if (book.getStatus() == Book.Status.ISSUED && book.getQuantity() <= 0) {
            throw new IllegalStateException("Book is already fully borrowed");
        }
        if (book.getQuantity()<=0){
            throw new IllegalStateException("No books available to borrorw");
        }
        book.setQuantity(book.getQuantity()-1);
        return bookRepository.save(book);

    }

    public Book returnBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with " + id + " not found"));

        book.setQuantity(book.getQuantity() + 1);
        if (book.getStatus() == Book.Status.ISSUED && book.getQuantity() > 0) {
            book.setStatus(Book.Status.AVAILABLE);
        }

        return bookRepository.save(book);
    }


}
