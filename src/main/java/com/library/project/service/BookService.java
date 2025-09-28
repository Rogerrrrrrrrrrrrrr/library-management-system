package com.library.project.service;

import com.library.project.dto.BookRequestDTO;
import com.library.project.dto.BookResponseDTO;
import com.library.project.entity.Book;
import com.library.project.exception.BookDeletionException;
import com.library.project.exception.BookNotFoundException;
import com.library.project.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class BookService {
//keep architrcture / patterns same as Optional Chaining
    //adding logger to debug
    private static final Logger logs = Logger.getLogger(BookService.class.getName());

    @Autowired
    private BookRepository bookRepository;

    public BookResponseDTO toBookResponseDTO(Book book) {
        return new BookResponseDTO(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getIsbn(),
                book.getQuantity(),
                book.getStatus() != null ? book.getStatus().name() : null
        );
    }

    public Book fromBookRequestDTO(BookRequestDTO request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setIsbn(request.getIsbn());
        book.setQuantity(request.getQuantity());
        return book;
    }

    public BookResponseDTO createBook(BookRequestDTO request) {

        logs.info("Creating book: " + request.getTitle());

        Book book = fromBookRequestDTO(request);
        validateBookForCreate(book);
        //add validations before accessing DB
        if (book.getIsbn() != null && bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with ISBN already exists");
        }
        if (book.getStatus() == null) {
            logs.fine("Book status not provided, setting default AVAILABLE");
            book.setStatus(Book.Status.AVAILABLE);
        }
        return toBookResponseDTO(bookRepository.save(book));
    }


    public BookResponseDTO getBookById(Long id){
        Book book =  bookRepository.findById(id)
                .orElseThrow(()-> new BookNotFoundException("Book with " + id + " not found"));

        return toBookResponseDTO(book);
    }

    public List<BookResponseDTO> getAllBook(){

        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            logs.info("No users found in the database");
        }
            return bookRepository.findAll()
                    .stream()
                    .map(this::toBookResponseDTO)
                    .collect(Collectors.toList());
    }

    public BookResponseDTO updateBook(Long id, BookRequestDTO book){
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(()-> new BookNotFoundException("Book with " + id + " not found"));

        validateBookForUpdate(existingBook);

    //use builder pattern here
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());

        existingBook.setCategory(book.getCategory());

        //System.out.println("Incoming quantity: " + book.getQuantity());
        existingBook.setQuantity(book.getQuantity());
        //System.out.println("Incoming quantity: " + book.getQuantity());
        existingBook.setIsbn(book.getIsbn());

        return toBookResponseDTO(bookRepository.save(existingBook));
    }


    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with " + id + " not found"));

        if (book.getStatus() == Book.Status.ISSUED) {
            throw new BookDeletionException("Cannot delete a borrowed book");
        }
        book.setDeleted(true);
        //marking status of book is deleted
//        if(book.isDeleted()){
//            book.setStatus(Book.Status.DELETED);
//        }

        bookRepository.save(book);
    }

    public Optional<BookResponseDTO> getBookByIsbn(String isbn) {
        return Optional.ofNullable(bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found")));
    }


    public BookResponseDTO borrowBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with " + id + " not found"));

        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("No books available to borrow");
        }
        if (book.getStatus() == Book.Status.ISSUED && book.getQuantity() <= 0) {
            throw new IllegalStateException("Book is already fully borrowed");
        }

        book.setQuantity(book.getQuantity() - 1);
        book.setStatus(Book.Status.ISSUED);

        return toBookResponseDTO(bookRepository.save(book));
    }

    public BookResponseDTO returnBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with " + id + " not found"));

        book.setQuantity(book.getQuantity() + 1);
        if (!book.isDeleted() && book.getQuantity() > 0) {
            book.setStatus(Book.Status.AVAILABLE);
        }

        return toBookResponseDTO(bookRepository.save(book));
    }

    private void validateBookForCreate(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        if (book.getQuantity() == null || book.getQuantity() < 0) {
            throw new IllegalArgumentException("Book quantity must be provided and non-negative");
        }
    }

    private void validateBookForUpdate(Book book) {
        if (book.getQuantity() < 0) {
            throw new IllegalArgumentException("Book quantity cannot be negative");
        }
        if (book.getStatus() == Book.Status.ISSUED && book.getQuantity() <= 0) {
            throw new IllegalArgumentException("Cannot set book status to ISSUED when no copies are available");
        }
    }


}
