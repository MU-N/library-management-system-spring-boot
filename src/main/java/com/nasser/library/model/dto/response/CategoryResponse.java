package com.nasser.library.model.dto.response;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String code,
        Integer bookCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}