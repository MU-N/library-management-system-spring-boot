package com.nasser.library.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Change Password Request Record - Password change data container
 * <p>
 * This record encapsulates the data required for changing a user's password.
 * It includes validation for the new password and its confirmation.
 * <p>
 * Used by:
 * - Change password endpoint to receive new password data
 * - User service for password change
 * <p>
 * Security Features:
 * - Password confirmation validation
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        String confirmPassword
) {
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}