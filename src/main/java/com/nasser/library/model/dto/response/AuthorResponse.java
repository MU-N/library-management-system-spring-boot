package com.nasser.library.model.dto.response;

public record AuthorResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String biography
) {}