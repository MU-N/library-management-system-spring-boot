package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.BookStatus;

import java.util.Set;

public record BookSummary(
        Long id,
        String title,
        String isbn,
        Set<String> authorNames,
        BookStatus status
) {
}
