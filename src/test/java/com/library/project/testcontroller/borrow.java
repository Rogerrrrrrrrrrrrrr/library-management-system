package com.library.project.testcontroller;

import com.library.project.controller.BorrowController;
import com.library.project.dto.BorrowRequestDTO;
import com.library.project.entity.Book;
import com.library.project.entity.BorrowRecord;
import com.library.project.entity.User;
import com.library.project.service.BorrowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BorrowControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BorrowService borrowService;

    @InjectMocks
    private BorrowController borrowController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(borrowController).build();
    }

    @Test
    void testGetRecordsByUser() throws Exception {
        User user = new User();
        user.setUserId(1L);

        Book book = new Book();
        book.setBookId(2L);

        BorrowRecord record = new BorrowRecord();
        record.setRecordId(100L);
        record.setUser(user);
        record.setBook(book);
        record.setIssuedDate(new Date());
        record.setStatus(BorrowRecord.Status.BORROWED);

        when(borrowService.getBorrowRecordsByUser(1L))
                .thenReturn(Collections.singletonList(record));

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recordId").value(100));
    }

    @Test
    void testBorrowBook() throws Exception {
        User user = new User();
        user.setUserId(1L);

        Book book = new Book();
        book.setBookId(2L);

        BorrowRecord record = new BorrowRecord();
        record.setRecordId(200L);
        record.setUser(user);
        record.setBook(book);
        record.setIssuedDate(new Date());
        record.setStatus(BorrowRecord.Status.BORROWED);

        BorrowRequestDTO request = new BorrowRequestDTO();
        request.setUserId(1L);
        request.setBookId(2L);


        when(borrowService.borrowBook(request)).thenReturn(record);

        mockMvc.perform(post("/api/records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"bookId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recordId").value(200));
    }

}
