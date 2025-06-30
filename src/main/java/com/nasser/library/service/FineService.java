package com.nasser.library.service;

import com.nasser.library.mapper.FineMapper;
import com.nasser.library.model.dto.request.FineRequest;
import com.nasser.library.model.dto.response.FineResponse;
import com.nasser.library.model.entity.BorrowRecord;
import com.nasser.library.model.entity.Fine;
import com.nasser.library.model.entity.FineStatus;
import com.nasser.library.model.entity.User;
import com.nasser.library.repository.BorrowRecordRepository;
import com.nasser.library.repository.FineRepository;
import com.nasser.library.repository.UserRepository;
import com.nasser.library.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service class for managing fine operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fineRepository;
    private final FineMapper fineMapper;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * Retrieves all fines with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of FineResponse objects
     */
    public Page<FineResponse> getAllFines(int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving fines - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);

        if (size > 1000) {
            log.warn("Large page size requested: {}, limiting to 1000", size);
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Fine> fines = fineRepository.findAll(pageable);
            Page<FineResponse> responses = fines.map(fineMapper::toResponse);
            
            log.debug("Successfully retrieved {} fines out of {} total", 
                     responses.getNumberOfElements(), responses.getTotalElements());
            return responses;
        } catch (Exception e) {
            log.error("Error retrieving fines: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve fines", e);
        }
    }

    /**
     * Creates a new fine.
     *
     * @param request The FineRequest containing the fine information
     * @return The created FineResponse
     */
    @Transactional
    public FineResponse createFine(FineRequest request) {
        log.debug("Creating new fine for user ID: {} with amount: {}", request.userId(), request.amount());

        try {
            // Validate user exists
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.userId()));

            // Validate borrow record if provided
            BorrowRecord borrowRecord = null;
            if (request.borrowRecordId() != null) {
                borrowRecord = borrowRecordRepository.findById(request.borrowRecordId())
                        .orElseThrow(() -> new IllegalArgumentException("Borrow record not found with ID: " + request.borrowRecordId()));
            }

            // Create fine
            Fine fine = fineMapper.toEntity(request);
            fine.setUser(user);
            fine.setBorrowRecord(borrowRecord);
            fine.setStatus(FineStatus.PENDING);

            Fine savedFine = fineRepository.save(fine);
            log.debug("Successfully created fine with ID: {}", savedFine.getId());

            return fineMapper.toResponse(savedFine);
        } catch (Exception e) {
            log.error("Error creating fine: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create fine", e);
        }
    }

    /**
     * Retrieves a fine by its ID.
     *
     * @param id The ID of the fine
     * @return The FineResponse
     */
    public FineResponse getFineById(Long id) {
        log.debug("Retrieving fine by ID: {}", id);

        try {
            Fine fine = fineRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Fine not found with ID: " + id));

            log.debug("Successfully retrieved fine with ID: {}", id);
            return fineMapper.toResponse(fine);
        } catch (Exception e) {
            log.error("Error retrieving fine by ID: {}", id, e);
            throw new RuntimeException("Failed to retrieve fine", e);
        }
    }

    /**
     * Updates a fine by its ID.
     *
     * @param id      The ID of the fine
     * @param request The FineRequest containing updated information
     * @return The updated FineResponse
     */
    @Transactional
    public FineResponse updateFine(Long id, FineRequest request) {
        log.debug("Updating fine with ID: {}", id);

        try {
            Fine fine = fineRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Fine not found with ID: " + id));

            fineMapper.updateEntityFromRequest(request, fine);
            Fine updatedFine = fineRepository.save(fine);

            log.debug("Successfully updated fine with ID: {}", id);
            return fineMapper.toResponse(updatedFine);
        } catch (Exception e) {
            log.error("Error updating fine with ID: {}", id, e);
            throw new RuntimeException("Failed to update fine", e);
        }
    }

    /**
     * Deletes a fine by its ID.
     *
     * @param id The ID of the fine to delete
     */
    @Transactional
    public void deleteFine(Long id) {
        log.debug("Deleting fine with ID: {}", id);

        try {
            Fine fine = fineRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Fine not found with ID: " + id));

            fineRepository.delete(fine);
            log.debug("Successfully deleted fine with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting fine with ID: {}", id, e);
            throw new RuntimeException("Failed to delete fine", e);
        }
    }

    /**
     * Retrieves fines for a specific user.
     *
     * @param userId  The ID of the user
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of FineResponse objects
     */
    public Page<FineResponse> getFinesByUser(Long userId, int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving fines for user ID: {}", userId);

        if (size > 1000) {
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            Page<Fine> fines = fineRepository.findByUserId(userId, pageable);
            return fines.map(fineMapper::toResponse);
        } catch (Exception e) {
            log.error("Error retrieving fines for user: {}", userId, e);
            throw new RuntimeException("Failed to retrieve user's fines", e);
        }
    }

    /**
     * Retrieves overdue fines.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return A Page of overdue FineResponse objects
     */
    public Page<FineResponse> getOverdueFines(int page, int size, String sortBy, String sortDir) {
        log.debug("Retrieving overdue fines");

        if (size > 1000) {
            size = 1000;
        }

        Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
        if (pageable == null) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        try {
            LocalDate currentDate = LocalDate.now();
            Page<Fine> overdueFines = fineRepository.findOverdueFines(currentDate, pageable);
            return overdueFines.map(fineMapper::toResponse);
        } catch (Exception e) {
            log.error("Error retrieving overdue fines: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve overdue fines", e);
        }
    }

    /**
     * Processes payment for a fine.
     *
     * @param id            The ID of the fine
     * @param paymentAmount The amount being paid
     * @return The updated FineResponse
     */
    @Transactional
    public FineResponse payFine(Long id, BigDecimal paymentAmount) {
        log.debug("Processing payment for fine ID: {} with amount: {}", id, paymentAmount);

        try {
            Fine fine = fineRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Fine not found with ID: " + id));

            if (fine.getStatus() == FineStatus.PAID) {
                throw new IllegalArgumentException("Fine is already paid");
            }

            if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Payment amount must be greater than zero");
            }

            BigDecimal remainingAmount = fine.getRemainingAmount();
            if (paymentAmount.compareTo(remainingAmount) > 0) {
                throw new IllegalArgumentException("Payment amount exceeds remaining fine amount");
            }

            // Process payment
            fine.markAsPaid(paymentAmount, "ONLINE", "AUTO_SYSTEM", "SYSTEM");

            Fine updatedFine = fineRepository.save(fine);
            log.debug("Successfully processed payment for fine ID: {}", id);

            return fineMapper.toResponse(updatedFine);
        } catch (Exception e) {
            log.error("Error processing payment for fine ID: {}", id, e);
            throw new RuntimeException("Failed to process fine payment", e);
        }
    }

    /**
     * Gets the total unpaid fine amount for a user.
     *
     * @param userId The ID of the user
     * @return The total unpaid amount
     */
    public BigDecimal getTotalUnpaidFinesByUser(Long userId) {
        log.debug("Getting total unpaid fines for user ID: {}", userId);

        try {
            return fineRepository.getTotalUnpaidFinesByUser(userId);
        } catch (Exception e) {
            log.error("Error getting total unpaid fines for user: {}", userId, e);
            throw new RuntimeException("Failed to get total unpaid fines", e);
        }
    }

    /**
     * Gets the count of pending fines for a user.
     *
     * @param userId The ID of the user
     * @return The count of pending fines
     */
    public long getPendingFineCountByUser(Long userId) {
        log.debug("Getting pending fine count for user ID: {}", userId);

        try {
            return fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING).size();
        } catch (Exception e) {
            log.error("Error getting pending fine count for user: {}", userId, e);
            throw new RuntimeException("Failed to get pending fine count", e);
        }
    }

    /**
     * Creates overdue fines for active borrow records that are past due.
     * This method should be called by a scheduled task.
     *
     * @param dailyFineRate The fine rate per day for overdue books
     * @return The number of fines created
     */
    @Transactional
    public int createOverdueFines(BigDecimal dailyFineRate) {
        log.debug("Creating overdue fines with daily rate: {}", dailyFineRate);

        try {
            // This would typically be called by a scheduled service
            // For now, it's a placeholder implementation
            log.info("Overdue fine creation scheduled task would run here");
            return 0;
        } catch (Exception e) {
            log.error("Error creating overdue fines: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create overdue fines", e);
        }
    }
} 