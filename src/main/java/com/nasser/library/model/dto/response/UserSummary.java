package com.nasser.library.model.dto.response;

// Lightweight nested versions
public record UserSummary(
        Long id,
        String firstName,
        String lastName,
        String email,
        String fullName,
        String membershipStatus
) {
}


