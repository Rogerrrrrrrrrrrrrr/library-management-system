package com.library.project.service;

import com.library.project.entity.Book;
import com.library.project.exception.BookNotFoundException;
import com.library.project.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book createBook(Book book){
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
    return bookRepository.findById(id)
            .orElseThrow(()-> new BookNotFoundException("Book with " + id + " not found"));
    }

    public void deleteBook(Long id){
    bookRepository.deleteById(id);
    }

}
