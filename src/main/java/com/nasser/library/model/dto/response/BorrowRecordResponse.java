package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.BorrowStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record BorrowRecordResponse(
        Long id,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BorrowStatus status,
        String notes,
        String returnConditionNotes,
        BigDecimal fineAmount,
        Boolean isFinePaid,
        String checkedOutBy,
        String checkedInBy,

        // Computed fields
        boolean isOverdue,
        long daysOverdue,
        boolean isActive,
        boolean isReturned,
        BigDecimal totalFinesAmount,
        int activeFinesCount,

        // Related entities
        UserResponse user,
        BookResponse book,
        Set<FineResponse> fines,

        // Audit fields
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
