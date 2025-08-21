package com.library.project.repository;

import com.library.project.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<BorrowRecord,Long> {

    List<BorrowRecord> findByUser_UserId(Long userId);
    List<BorrowRecord> findByBook_BookId(Long bookId);

    boolean existsByBook_BookIdAndStatus(Long bookId, BorrowRecord.Status status);

    boolean existsByUser_UserIdAndBook_BookIdAndStatus(Long userId, Long bookId, BorrowRecord.Status status);

    int countByUser_UserIdAndStatus(Long userId, BorrowRecord.Status status);
}
