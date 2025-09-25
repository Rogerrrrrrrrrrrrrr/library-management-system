package com.library.project.controller;

import com.library.project.entity.Book;
import com.library.project.exception.BookNotFoundException;
import com.library.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "api/books")
    public ResponseEntity<List<Book>> getAllBook(){
        List<Book> bookList = bookService.getAllBook();
        try{
            if (CollectionUtils.isEmpty(bookList)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.of(Optional.of(bookList));
    }

    @GetMapping(value = "api/books/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id){
        Book b = bookService.getBookById(id);
        if (Objects.isNull(b)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.of(Optional.of(b));
    }

    @GetMapping("api/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("api/books")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        try {
//          createBook =  bookService.createBook(book);
          return ResponseEntity.status(HttpStatus.CREATED).body( bookService.createBook(book));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PutMapping("api/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") long id,@RequestBody Book book){
        try {
            Book updated = bookService.updateBook(id, book);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Incoming quantity: " + book.getQuantity());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        //        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @DeleteMapping("api/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") long id){
        try{
            //validate if book is borrowed or not before deleting
            //bookService.getBookById(id);
            //redundant usage of getBookById

            bookService.deleteBook(id);
            return ResponseEntity.status(HttpStatus.OK).build();
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
