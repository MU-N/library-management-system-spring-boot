package com.nasser.library.model.dto.response;

import com.nasser.library.model.entity.Role;
import com.nasser.library.model.entity.AuthProvider;
import java.time.LocalDateTime;

public record UserProfileResponse(
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
        String fullName,
        int totalBooksBorrowed,
        int currentlyBorrowed,
        boolean hasActiveFines
) {}