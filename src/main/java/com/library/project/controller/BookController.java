package com.library.project.controller;

import com.library.project.entity.Book;
import com.library.project.exception.BookNotFoundException;
import com.library.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "api/books")
    public ResponseEntity<List<Book>> getAllBook(){
        List<Book> bookList = bookService.getAllBook();

            if (CollectionUtils.isEmpty(bookList)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }
        return ResponseEntity.of(Optional.of(bookList));
    }

    @GetMapping("api/books/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id) {
        try {
            Book book = bookService.getBookById(id);
            return ResponseEntity.ok(book);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("api/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/api/books")
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            if (book.getTitle() == null || book.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
                return ResponseEntity.badRequest().body("Author is required");
            }

            if (book.getQuantity() == null || book.getQuantity() < 0) {
                return ResponseEntity.badRequest()
                        .body("check quantity either it is not provided or cannot be negative");
            }

            Book created = bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
         catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while creating book");
        }
    }


    @PutMapping("api/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable("id") long id, @RequestBody Book book){
        try {
            if (book.getQuantity() < 0) {
                return ResponseEntity.badRequest().body("Quantity cannot be negative");
            }
            Book updated = bookService.updateBook(id, book);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(updated);
        }
        catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            // Business logic violation, like negative quantity or invalid status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }
        catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Incoming quantity: " + book.getQuantity());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while updating the book.");
        }
        //        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @DeleteMapping("api/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id){
        try{
            //validate if book is borrowed or not before deleting--handled in service
            //bookService.getBookById(id);
            //redundant usage of getBookById-cleared

            bookService.deleteBook(id);
            return ResponseEntity.status(HttpStatus.OK).body("Book with id " + id + " has been deleted");
        }catch (BookNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            //e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
