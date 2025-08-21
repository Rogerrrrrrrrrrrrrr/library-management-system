package com.library.project.controller;

import com.library.project.entity.Book;
import com.library.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "api/books")
    public ResponseEntity<List<Book>> getAllBook(){
        List<Book> bookList = bookService.getAllBook();
        try{
            if (bookList.size()<=0){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.of(Optional.of(bookList));
    }

    @GetMapping(value = "api/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id){
        Book b = bookService.getBookById(id);
        if (b==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(b);
        }
        return ResponseEntity.of(Optional.of(b));
    }

    @PostMapping("api/books")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createBook=null;
        try {
          createBook =  bookService.createBook(book);
          return ResponseEntity.status(HttpStatus.CREATED).body(createBook);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PutMapping("api/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") long id,@RequestBody Book book){
        try{
            bookService.updateBook(id,book);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @DeleteMapping("api/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") long id){
        try{
            bookService.getBookById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
