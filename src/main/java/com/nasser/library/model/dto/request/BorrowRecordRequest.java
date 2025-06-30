package com.nasser.library.model.dto.request;

import com.nasser.library.model.entity.BorrowStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BorrowRecordRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Book ID is required")
        Long bookId,

        @NotNull(message = "Borrow date is required")
        LocalDate borrowDate,

        @NotNull(message = "Due date is required")
        LocalDate dueDate,

        LocalDate returnDate,

        @NotNull(message = "Borrow status is required")
        @Enumerated(EnumType.STRING)
        BorrowStatus status,

        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        String notes,

        @Size(max = 500, message = "Return condition notes must not exceed 500 characters")
        String returnConditionNotes,

        @DecimalMin(value = "0.0", message = "Fine amount cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Fine amount format is invalid")
        BigDecimal fineAmount,

        Boolean isFinePaid,

        String checkedOutBy,// Librarian who processed the checkout

        String checkedInBy // Librarian who processed the return

) {
        // Custom validation method
        public boolean isValidDateRange() {
                if (borrowDate != null && dueDate != null) {
                        return !dueDate.isBefore(borrowDate);
                }
                return true;
        }

        public boolean isValidReturnDate() {
                if (returnDate != null && borrowDate != null) {
                        return !returnDate.isBefore(borrowDate);
                }
                return true;
        }
}
