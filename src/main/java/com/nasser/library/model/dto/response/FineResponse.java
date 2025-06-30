package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.FineStatus;
import com.nasser.library.model.entity.FineType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FineResponse(
        Long id,
        BigDecimal amount,
        FineType type,
        FineStatus status,
        String reason,
        String description,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate paidDate,
        BigDecimal paidAmount,
        String processedBy,

        // Computed fields
        BigDecimal remainingAmount,
        boolean isPending,
        boolean isPaid,
        boolean isOverdue,
        boolean isFullyPaid,
        long daysSinceIssue,
        long daysUntilDue,

        // Clean nested objects
        UserSummary user,
        BorrowRecordSummary borrowRecord,
        BookSummary book
) {
}
