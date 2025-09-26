package com.library.project.testcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.project.controller.BookController;
import com.library.project.entity.Book;
import com.library.project.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void testGetBookById() throws Exception {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Sapiens");
        book.setAuthor("Yuval Noah Harari");
        book.setQuantity(5);
        book.setStatus(Book.Status.AVAILABLE);

        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sapiens"))
                .andExpect(jsonPath("$.author").value("Yuval Noah Harari"));
    }

    @Test
    void testCreateBook() throws Exception {
        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor("Author");
        book.setQuantity(3);

        when(bookService.createBook(book)).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"));
    }
}
