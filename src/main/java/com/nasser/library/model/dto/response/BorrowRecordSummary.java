package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.BorrowStatus;

import java.time.LocalDate;

public record BorrowRecordSummary(
        Long id,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BorrowStatus status,
        boolean isOverdue
) {
}
