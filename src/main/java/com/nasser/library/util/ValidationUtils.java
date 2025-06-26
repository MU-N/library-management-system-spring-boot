package com.nasser.library.util;

import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility class containing common validation and pagination methods for controllers.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Validates pagination parameters for page and size.
     *
     * @param page The page number (must be >= 0)
     * @param size The page size (must be between 1 and 100)
     * @param log  The logger instance for warning messages
     * @return true if validation fails, false if parameters are valid
     */
    public static boolean isInvalidPaginationParameters(int page, int size, Logger log) {
        if (page < 0) {
            log.warn("Invalid page number: {}. Page must be >= 0", page);
            return true;
        }
        if (size <= 0 || size > 100) {
            log.warn("Invalid page size: {}. Size must be between 1 and 100", size);
            return true;
        }
        return false;
    }

    /**
     * Validates sort direction parameter.
     *
     * @param sortDir The sort direction string
     * @param log     The logger instance for warning messages
     * @return true if invalid, false if valid
     */
    public static boolean isInvalidSortDirection(String sortDir, Logger log) {
        if (sortDir == null || (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc"))) {
            log.warn("Invalid sort direction: {}. Must be 'asc' or 'desc'", sortDir);
            return true;
        }
        return false;
    }

    /**
     * Creates a Pageable object with validation and error handling.
     * This method consolidates all pagination logic including validation,
     * sort direction parsing, and Pageable creation.
     *
     * @param page    The page number (0-based)
     * @param size    The page size
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction ("asc" or "desc")
     * @param log     The logger instance for warning messages
     * @return Pageable object if validation passes, null if validation fails
     */
    public static Pageable createPageable(int page, int size, String sortBy, String sortDir, Logger log) {
        // Validate pagination parameters
        if (isInvalidPaginationParameters(page, size, log)) {
            return null;
        }

        // Create sort direction with fallback to ASC
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid sort direction: {}. Using default 'asc'", sortDir);
            direction = Sort.Direction.ASC;
        }

        // Create and return pageable object
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    /**
     * Validates all pagination and sorting parameters at once.
     *
     * @param page    The page number (0-based)
     * @param size    The page size
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction ("asc" or "desc")
     * @param log     The logger instance for warning messages
     * @return true if any validation fails, false if all parameters are valid
     */
    public static boolean isInvalidPaginationRequest(int page, int size, String sortBy, String sortDir, Logger log) {
        return isInvalidPaginationParameters(page, size, log) ||
                (sortBy == null || sortBy.trim().isEmpty());
    }
}