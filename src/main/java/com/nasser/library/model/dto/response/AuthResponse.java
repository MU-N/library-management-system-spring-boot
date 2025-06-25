package com.nasser.library.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Authentication Response Record - Contains JWT tokens and user info
 *
 * This record is returned to the client after successful authentication.
 * It contains the access token, refresh token, and essential user information.
 * Records provide immutability and cleaner syntax for response DTOs.
 *
 * Components:
 * - Access Token: Short-lived token for API access
 * - Refresh Token: Long-lived token for renewing access tokens
 * - User Information: Basic user details for client-side display
 * - Token Metadata: Expiration times and token type information
 *
 * Security Considerations:
 * - Only essential user info is included (no sensitive data)
 * - Expiration times help clients manage token lifecycle
 * - Token type specified for proper usage
 * - Immutable by design
 */
public record AuthResponse(

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn,

        @JsonProperty("user_id")
        Long userId,

        String email,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        String role,

        String provider,

        @JsonProperty("max_books_allowed")
        Integer maxBooksAllowed,

        @JsonProperty("authenticated_at")
        LocalDateTime authenticatedAt,

        @JsonProperty("is_first_login")
        Boolean isFirstLogin
) {
    /**
     * Compact constructor with default values
     * Provides default values for optional fields
     */
    public AuthResponse {
        // Set default values for optional fields
        if (tokenType == null) {
            tokenType = "Bearer";
        }
        if (authenticatedAt == null) {
            authenticatedAt = LocalDateTime.now();
        }
        if (isFirstLogin == null) {
            isFirstLogin = false;
        }
    }

    /**
     * Static factory method for creating AuthResponse with builder-like pattern
     * Maintains the convenience of the previous builder while using records
     */
    public static AuthResponse of(String accessToken,  Long expiresIn,
                                  Long userId, String email, String firstName, String lastName,
                                  String role, String provider, Integer maxBooksAllowed,
                                  Boolean isFirstLogin) {
        return new AuthResponse(
                accessToken,
                "Bearer",
                expiresIn,
                userId,
                email,
                firstName,
                lastName,
                role,
                provider,
                maxBooksAllowed,
                LocalDateTime.now(),
                isFirstLogin != null ? isFirstLogin : false
        );
    }
}