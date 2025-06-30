package com.nasser.library.model.dto.request;

import com.nasser.library.model.entity.FineStatus;
import com.nasser.library.model.entity.FineType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FineRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        Long borrowRecordId, // Optional - some fines may not be related to specific borrow records

        @NotNull(message = "Fine amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Fine amount must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Fine amount format is invalid")
        BigDecimal amount,

        @NotNull(message = "Fine type is required")
        FineType type,

        @NotNull(message = "Fine status is required")
        FineStatus status,

        @NotBlank(message = "Reason is required")
        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        @Column(name = "description", length = 1000)
        String description,

        @NotNull(message = "Issue date is required")
        @PastOrPresent(message = "Issue date cannot be in the future")
        LocalDate issueDate,

        @Future(message = "Due date must be in the future")
        LocalDate dueDate,

        @PastOrPresent(message = "Paid date cannot be in the future")
        LocalDate paidDate,


        @DecimalMin(value = "0.0", message = "Paid amount cannot be negative")
        @Digits(integer = 8, fraction = 2, message = "Paid amount format is invalid")
        BigDecimal paidAmount,

        @Size(max = 100, message = "Processed by must not exceed 100 characters")
        String processedBy

) {
}
