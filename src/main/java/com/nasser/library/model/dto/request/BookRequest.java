package com.nasser.library.model.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record BookRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 500, message = "Title must not exceed 500 characters")
        String title,

        //987-654-321-0
        //111-222-333-X
        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{3}-[0-9X]$", message = "Invalid ISBN-10 format, must be XXX-XXX-XXX-X")
        String isbn,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotBlank(message = "Publisher is required")
        @Size(max = 200, message = "Publisher name must not exceed 200 characters")
        String publisher,

        @NotNull(message = "Publication year is required")
        @PastOrPresent(message = "Publication year must be in the past or present")
        Integer publicationYear,

        LocalDate publicationDate,

        @NotNull(message = "Number of pages is required")
        @Min(value = 1, message = "Number of pages must be at least 1")
        @Max(value = 10000, message = "Number of pages must not exceed 10000")
        Integer pages,

        @Size(max = 10, message = "Language code must not exceed 10 characters")
        String language,

        @Size(max = 50, message = "Edition must not exceed 50 characters")
        String edition,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
        BigDecimal price,

        @Min(value = 0, message = "Total copies cannot be negative")
        Integer totalCopies,

        @Min(value = 0, message = "Available copies cannot be negative")
        Integer availableCopies,

        @Size(max = 50)
        String locationShelf,

        @Size(max = 50)
        String locationSection,

        Set<Long> authorIds,    // IDs of authors to associate
        Set<Long> categoryIds   // IDs of categories to associate
) {
}
