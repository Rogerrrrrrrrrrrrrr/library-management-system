package com.library.project.repository;

import com.library.project.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BorrowRepository extends JpaRepository<BorrowRecord,Long> {

    List<BorrowRecord> findByUser_UserId(Long userId);
    List<BorrowRecord> findByBook_BookId(Long bookId);

    boolean existsByBook_BookIdAndStatus(Long bookId, BorrowRecord.Status status);

    boolean existsByUser_UserIdAndBook_BookIdAndStatus(Long userId, Long bookId, BorrowRecord.Status status);

    int countByUser_UserIdAndStatus(Long userId, BorrowRecord.Status status);
    
    boolean existsByUser_UserIdAndBook_BookIdAndStatusIn(Long userId, Long bookId, List<BorrowRecord.Status> statuses);


    Optional<BorrowRecord> findByUser_UserIdAndBook_BookIdAndStatusIn(Long userId, Long bookId, List<BorrowRecord.Status> statuses);

    List<BorrowRecord> findByStatus(BorrowRecord.Status status);
}
