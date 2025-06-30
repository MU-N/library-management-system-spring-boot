package com.nasser.library.service;

import com.nasser.library.mapper.BorrowRecordMapper;
import com.nasser.library.model.dto.request.BorrowRecordRequest;
import com.nasser.library.model.dto.response.BorrowRecordResponse;
import com.nasser.library.model.entity.*;
import com.nasser.library.repository.BorrowRecordRepository;
import com.nasser.library.repository.BookRepository;
import com.nasser.library.repository.UserRepository;
import com.nasser.library.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service class for managing borrow record operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all borrow records with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of BorrowRecordResponse objects
     */
    public Page<BorrowRecordResponse> getAllBorrowRecords(int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving borrow records - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<BorrowRecord> records = borrowRecordRepository.findAll(pageable);
            Page<BorrowRecordResponse> responses = records.map(borrowRecordMapper::toResponse);
            
            log.debug("Successfully retrieved {} borrow records out of {} total", 
                     responses.getNumberOfElements(), responses.getTotalElements());
            return responses;
        } catch (Exception e) {
            log.error("Error retrieving borrow records: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve borrow records", e);
        }
    }

    /**
     * Creates a new borrow record.
     *
     * @param request The BorrowRecordRequest containing the borrow information
     * @return The created BorrowRecordResponse
     */
    @Transactional
    public BorrowRecordResponse createBorrowRecord(BorrowRecordRequest request) {
        log.debug("Creating new borrow record for user ID: {} and book ID: {}", request.userId(), request.bookId());

        try {
            // Validate user exists
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.userId()));

            // Validate book exists and is available
            Book book = bookRepository.findById(request.bookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + request.bookId()));

            if (book.getStatus() != BookStatus.AVAILABLE) {
                throw new IllegalArgumentException("Book is not available for borrowing");
            }

            // Check if user has reached their borrowing limit
            long activeRecords = borrowRecordRepository.countActiveRecordsByUser(user.getId());
            if (activeRecords >= user.getMaxBooksAllowed()) {
                throw new IllegalArgumentException("User has reached maximum borrowing limit of " + user.getMaxBooksAllowed());
            }

            // Create borrow record
            BorrowRecord borrowRecord = borrowRecordMapper.toEntity(request);
            borrowRecord.setUser(user);
            borrowRecord.setBook(book);
            borrowRecord.setStatus(BorrowStatus.ACTIVE);

            // Update book status
            book.setStatus(BookStatus.BORROWED);
            bookRepository.save(book);

            BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
            log.debug("Successfully created borrow record with ID: {}", savedRecord.getId());

            return borrowRecordMapper.toResponse(savedRecord);
        } catch (Exception e) {
            log.error("Error creating borrow record: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create borrow record", e);
        }
    }

    /**
     * Retrieves a borrow record by its ID.
     *
     * @param id The ID of the borrow record
     * @return The BorrowRecordResponse
     */
    public BorrowRecordResponse getBorrowRecordById(Long id) {
        log.debug("Retrieving borrow record by ID: {}", id);

        try {
            BorrowRecord record = borrowRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with ID: " + id));

            log.debug("Successfully retrieved borrow record with ID: {}", id);
            return borrowRecordMapper.toResponse(record);
        } catch (Exception e) {
            log.error("Error retrieving borrow record by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve borrow record", e);
        }
    }

    /**
     * Updates a borrow record by its ID.
     *
     * @param id      The ID of the borrow record
     * @param request The BorrowRecordRequest containing updated information
     * @return The updated BorrowRecordResponse
     */
    @Transactional
    public BorrowRecordResponse updateBorrowRecord(Long id, BorrowRecordRequest request) {
        log.debug("Updating borrow record with ID: {}", id);

        try {
            BorrowRecord record = borrowRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with ID: " + id));

            borrowRecordMapper.updateEntityFromRequest(request, record);
            BorrowRecord updatedRecord = borrowRecordRepository.save(record);

            log.debug("Successfully updated borrow record with ID: {}", id);
            return borrowRecordMapper.toResponse(updatedRecord);
        } catch (Exception e) {
            log.error("Error updating borrow record with ID: {}", id, e);
            throw new RuntimeException("Failed to update borrow record", e);
        }
    }

    /**
     * Deletes a borrow record by its ID.
     *
     * @param id The ID of the borrow record to delete
     */
    @Transactional
    public void deleteBorrowRecord(Long id) {
        log.debug("Deleting borrow record with ID: {}", id);

        try {
            BorrowRecord record = borrowRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with ID: " + id));

            // If record is active, make book available again
            if (record.getStatus() == BorrowStatus.ACTIVE) {
                Book book = record.getBook();
                book.setStatus(BookStatus.AVAILABLE);
                bookRepository.save(book);
            }

            borrowRecordRepository.delete(record);
            log.debug("Successfully deleted borrow record with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting borrow record with ID: {}", id, e);
            throw new RuntimeException("Failed to delete borrow record", e);
        }
    }

    /**
     * Retrieves borrow records for a specific user.
     *
     * @param userId  The ID of the user
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of BorrowRecordResponse objects
     */
    public Page<BorrowRecordResponse> getBorrowRecordsByUser(Long userId, int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving borrow records for user ID: {}", userId);

        if (size > 1000) {
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<BorrowRecord> records = borrowRecordRepository.findByUserId(userId, pageable);
            return records.map(borrowRecordMapper::toResponse);
        } catch (Exception e) {
            log.error("Error retrieving borrow records for user: {}", userId, e);
            throw new RuntimeException("Failed to retrieve user's borrow records", e);
        }
    }

    /**
     * Retrieves overdue borrow records.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of overdue BorrowRecordResponse objects
     */
    public Page<BorrowRecordResponse> getOverdueBorrowRecords(int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving overdue borrow records");

        if (size > 1000) {
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            LocalDate currentDate = LocalDate.now();
            Page<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(currentDate, pageable);
            return overdueRecords.map(borrowRecordMapper::toResponse);
        } catch (Exception e) {
            log.error("Error retrieving overdue borrow records: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve overdue borrow records", e);
        }
    }

    /**
     * Returns a book (marks borrow record as returned).
     *
     * @param id The ID of the borrow record
     * @return The updated BorrowRecordResponse
     */
    @Transactional
    public BorrowRecordResponse returnBook(Long id) {
        log.debug("Processing book return for borrow record ID: {}", id);

        try {
            BorrowRecord record = borrowRecordRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with ID: " + id));

            if (record.getStatus() != BorrowStatus.ACTIVE) {
                throw new IllegalArgumentException("Book is not currently borrowed");
            }

            // Mark as returned
            record.markAsReturned("SYSTEM"); // You might want to pass the current user's name
            
            // Make book available again
            Book book = record.getBook();
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);

            BorrowRecord updatedRecord = borrowRecordRepository.save(record);
            log.debug("Successfully processed book return for borrow record ID: {}", id);

            return borrowRecordMapper.toResponse(updatedRecord);
        } catch (Exception e) {
            log.error("Error processing book return for ID: {}", id, e);
            throw new RuntimeException("Failed to process book return", e);
        }
    }

    /**
     * Gets the count of active borrow records for a user.
     *
     * @param userId The ID of the user
     * @return The count of active records
     */
    public long getActiveRecordCountByUser(Long userId) {
        log.debug("Getting active record count for user ID: {}", userId);

        try {
            return borrowRecordRepository.countActiveRecordsByUser(userId);
        } catch (Exception e) {
            log.error("Error getting active record count for user: {}", userId, e);
            throw new RuntimeException("Failed to get active record count", e);
        }
    }
} 