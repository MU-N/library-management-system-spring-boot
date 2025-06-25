package com.nasser.library.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Registration Request Record - User registration data container
 * <p>
 * This record captures all necessary information for creating a new user account.
 * Records provide immutability and cleaner syntax while maintaining comprehensive validation.
 * <p>
 * Used by:
 * - Registration endpoint to receive new user data
 * - User service for account creation
 * <p>
 * Security Features:
 * - Strong password requirements
 * - Email format validation
 * - Input length restrictions
 * - Phone number format validation
 * - Immutable by design
 */
public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,


        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
        )
        String password,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        @Pattern(
                regexp = "^[+]?[1-9]\\d{1,14}$|^$",
                message = "Phone number must be in valid international format"
        )
        String phone
) {
    /**
     * Validates that password and confirmPassword match
     * This method can be used in custom validation logic
     *
     * @return true if passwords match, false otherwise
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * Static factory method for creating RegisterRequest with optional phone
     * Provides convenience for cases where phone is not provided
     */
    public static RegisterRequest of(String firstName, String lastName, String email,
                                     String password, String confirmPassword) {
        return new RegisterRequest(firstName, lastName, email, password, confirmPassword, null);
    }

    /**
     * Static factory method for creating complete RegisterRequest
     */
    public static RegisterRequest of(String firstName, String lastName, String email,
                                     String password, String confirmPassword, String phone) {
        return new RegisterRequest(firstName, lastName, email, password, confirmPassword, phone);
    }
}