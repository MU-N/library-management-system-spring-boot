package com.nasser.library.model.dto.request;

import com.nasser.library.model.entity.Role;
import jakarta.validation.constraints.*;

/**
 * Update User Request Record - User data container for updating user information
 * <p>
 * This record carries user data from the client to the update user endpoint.
 */
public record UpdateUserRequest(
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Email(message = "Email should be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phone,

        Role role,

        @Min(value = 1, message = "Max books allowed must be at least 1")
        @Max(value = 20, message = "Max books allowed cannot exceed 20")
        Integer maxBooksAllowed

) {}