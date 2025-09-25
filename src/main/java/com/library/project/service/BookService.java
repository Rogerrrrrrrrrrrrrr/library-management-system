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

    public Book createBook(Book book){
        //add validations before accessing DB
        return this.bookRepository.save(book);
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
    //use builder pattern here
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());

        existingBook.setCategory(book.getCategory());

        System.out.println("Incoming quantity: " + book.getQuantity());
        existingBook.setQuantity(book.getQuantity());
        System.out.println("Incoming quantity: " + book.getQuantity());
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

        return bookRepository.save(book);
    }

}
