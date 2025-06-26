package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.Role;
import java.time.LocalDateTime;

/**
 * User List Response Record - User data container for user information
 * <p>
 * This record carries user data from the server to the client.
 */
public record UserListResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        LocalDateTime createdAt,
        String fullName
) {}