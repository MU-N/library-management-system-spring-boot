package com.nasser.library.repository;

import com.nasser.library.model.entity.BorrowRecord;
import com.nasser.library.model.entity.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /**
     * Finds all borrow records for a specific user by user ID.
     *
     * @param userId   the ID of the user to find records for; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of borrow records for the user
     */
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds all borrow records for a specific book by book ID.
     *
     * @param bookId   the ID of the book to find records for; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of borrow records for the book
     */
    Page<BorrowRecord> findByBookId(Long bookId, Pageable pageable);

    /**
     * Finds all borrow records with a specific status.
     *
     * @param status   the borrow status to filter by; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of borrow records with the specified status
     */
    Page<BorrowRecord> findByStatus(BorrowStatus status, Pageable pageable);

    /**
     * Finds all borrow records for a specific user with a specific status.
     *
     * @param userId the ID of the user to find records for; cannot be null
     * @param status the borrow status to filter by; cannot be null
     * @return a list of borrow records for the user with the specified status
     */
    List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    /**
     * Finds all overdue borrow records (active records past due date).
     *
     * @param currentDate the current date to compare against; cannot be null
     * @param pageable    the pagination information; cannot be null
     * @return a pageable list of overdue borrow records
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'ACTIVE' AND br.dueDate < :currentDate")
    Page<BorrowRecord> findOverdueRecords(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    /**
     * Counts the number of active borrow records for a user.
     *
     * @param userId the ID of the user; cannot be null
     * @return the count of active borrow records for the user
     */
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 'ACTIVE'")
    long countActiveRecordsByUser(@Param("userId") Long userId);
} 