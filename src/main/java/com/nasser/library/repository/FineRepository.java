package com.nasser.library.repository;

import com.nasser.library.model.entity.Fine;
import com.nasser.library.model.entity.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    /**
     * Finds all fines for a specific user by user ID.
     *
     * @param userId   the ID of the user to find fines for; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of fines for the user
     */
    Page<Fine> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds all fines for a specific borrow record by ID.
     *
     * @param borrowRecordId the ID of the borrow record to find fines for; cannot be null
     * @param pageable       the pagination information; cannot be null
     * @return a pageable list of fines for the borrow record
     */
    Page<Fine> findByBorrowRecordId(Long borrowRecordId, Pageable pageable);

    /**
     * Finds all fines with a specific status.
     *
     * @param status   the fine status to filter by; cannot be null
     * @param pageable the pagination information; cannot be null
     * @return a pageable list of fines with the specified status
     */
    Page<Fine> findByStatus(FineStatus status, Pageable pageable);

    /**
     * Finds all fines for a specific user with a specific status.
     *
     * @param userId the ID of the user to find fines for; cannot be null
     * @param status the fine status to filter by; cannot be null
     * @return a list of fines for the user with the specified status
     */
    List<Fine> findByUserIdAndStatus(Long userId, FineStatus status);

    /**
     * Finds all overdue fines (pending fines past their due date).
     *
     * @param currentDate the current date to compare against; cannot be null
     * @param pageable    the pagination information; cannot be null
     * @return a pageable list of overdue fines
     */
    @Query("SELECT f FROM Fine f WHERE f.status = 'PENDING' AND f.dueDate < :currentDate")
    Page<Fine> findOverdueFines(@Param("currentDate") LocalDate currentDate, Pageable pageable);

    /**
     * Calculates the total unpaid fine amount for a specific user.
     *
     * @param userId the ID of the user; cannot be null
     * @return the total unpaid fine amount for the user
     */
    @Query("SELECT COALESCE(SUM(f.amount - f.paidAmount), 0) FROM Fine f WHERE f.user.id = :userId AND f.status = 'PENDING'")
    BigDecimal getTotalUnpaidFinesByUser(@Param("userId") Long userId);
} 