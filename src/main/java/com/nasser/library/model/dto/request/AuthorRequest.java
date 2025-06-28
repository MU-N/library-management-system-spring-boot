package com.nasser.library.model.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AuthorRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Size(max = 100, message = "Pen name must not exceed 100 characters")
        String penName,

        @Size(max = 3000, message = "Biography must not exceed 3000 characters")
        String biography,

        @Email(message = "Email should be valid")
        @Size(max = 200, message = "Email must not exceed 200 characters")
        String email,

        @Size(max = 100, message = "Nationality must not exceed 100 characters")
        String nationality,

        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @Past(message = "Death date must be in the past")
        LocalDate deathDate,

        @Size(max = 200, message = "Birth place must not exceed 200 characters")
        String birthPlace
) {
}