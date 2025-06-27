package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.Author;
import com.nasser.library.model.entity.BookStatus;
import com.nasser.library.model.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record BookResponse(

        Long id,
        String title,
        String isbn,
        String description,
        String publisher,
        Integer publicationYear,
        LocalDate publicationDate,
        Integer pages,
        String language,
        String edition,
        BigDecimal price,
        BookStatus status,
        Integer totalCopies,
        Integer availableCopies,
        String locationShelf,
        String locationSection,
        BigDecimal averageRating,
        Integer ratingCount,
        boolean isAvailable,
        Set<AuthorResponse> authors,
        Set<CategoryResponse> categories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
