package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.Role;
import com.nasser.library.model.entity.AuthProvider;

import java.time.LocalDateTime;


/**
 * User Response Record - User data container for user information
 * <p>
 * This record carries user data from the server to the client.
 */
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Role role,
        Integer maxBooksAllowed,
        AuthProvider provider,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String fullName  // This will be computed by MapStruct
) {}