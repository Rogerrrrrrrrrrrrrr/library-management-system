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
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "books")
    public ResponseEntity<List<Book>> getAllBook(){
        List<Book> bookList = bookService.getAllBook();

            if (CollectionUtils.isEmpty(bookList)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }
        //return ResponseEntity.of(Optional.of(bookList));
        return ResponseEntity.ok(bookList);
    }

    @GetMapping("books/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id) {

            Book book = bookService.getBookById(id);
            return ResponseEntity.ok(book);
    }

    @GetMapping("isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.of(book);
    }


    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book created = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") long id, @RequestBody Book book) {
        Book updated = bookService.updateBook(id, book);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id){

            //validate if book is borrowed or not before deleting--handled in service
            //bookService.getBookById(id);
            //redundant usage of getBookById-cleared

            bookService.deleteBook(id);
            return ResponseEntity.status(HttpStatus.OK).body("Book with id " + id + " has been deleted");

        //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
