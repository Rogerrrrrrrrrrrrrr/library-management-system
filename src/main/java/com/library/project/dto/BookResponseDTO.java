package com.library.project.dto;
import com.library.project.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


    public class BookResponseDTO {


        private Long bookId;
        private String title;
        private String author;
        private String category;
        private String isbn;
        private Integer quantity;
        private String status;

        public BookResponseDTO(Long bookId, String title, String author, String category, String isbn, Integer quantity, String status) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.category = category;
            this.isbn = isbn;
            this.quantity = quantity;
            this.status = status;
        }

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

