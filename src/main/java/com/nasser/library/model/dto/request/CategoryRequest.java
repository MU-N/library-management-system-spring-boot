package com.nasser.library.model.dto.request;

import jakarta.validation.constraints.*;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Size(max = 50, message = "Code must not exceed 50 characters")
        @Pattern(regexp = "^[A-Z0-9_]*$", message = "Code must contain only uppercase letters, numbers, and underscores")
        String code
) {
}