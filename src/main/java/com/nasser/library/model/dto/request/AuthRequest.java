package com.nasser.library.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Authentication Request Record - Login credentials container
 *
 * This record carries user login credentials from the client to the authentication endpoint.
 * Records provide immutability, automatic equals/hashCode, and cleaner syntax for DTOs.
 *
 * Used by:
 * - Login endpoint to receive user credentials
 * - Authentication service for credential validation
 *
 * Security Features:
 * - Email format validation
 * - Password length requirements
 * - Input sanitization through validation
 * - Immutable by design
 */
public record AuthRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password
) {
}