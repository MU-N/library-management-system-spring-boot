package com.nasser.library.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AuthorResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String penName,
        String biography,
        String email,
        String nationality,
        LocalDate birthDate,
        LocalDate deathDate,
        String birthPlace,
        Integer bookCount,
        BigDecimal averageRating,
        String displayName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}