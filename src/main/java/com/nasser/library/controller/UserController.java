package com.nasser.library.controller;

import com.nasser.library.model.entity.User;
import com.nasser.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves a paginated list of all users with sorting support.
     *
     * @param page    Page number (0-based, default: 0)
     * @param size    Number of users per page (default: 10, max: 100)
     * @param sortBy  Field to sort by (default: "id")
     * @param sortDir Sort direction - "asc" or "desc" (default: "asc")
     * @return ResponseEntity containing a Page of User entities with pagination metadata
     */
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Retrieving users - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);
        try {
            // Validate pagination parameters
            if (page < 0) {
                log.warn("Invalid page number: {}. Page must be >= 0", page);
                return ResponseEntity.badRequest().build();
            }
            if (size <= 0 || size > 100) {
                log.warn("Invalid page size: {}. Size must be between 1 and 100", size);
                return ResponseEntity.badRequest().build();
            }

            // Create sort direction
            Sort.Direction direction;
            try {
                direction = Sort.Direction.fromString(sortDir);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid sort direction: {}. Using default 'asc'", sortDir);
                direction = Sort.Direction.ASC;
            }

            // Create pageable object
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Get paginated users
            Page<User> usersPage = userService.getAllUsers(pageable);

            log.info("Successfully retrieved {} users out of {} total users. Page {}/{}",
                    usersPage.getNumberOfElements(),
                    usersPage.getTotalElements(),
                    usersPage.getNumber() + 1,
                    usersPage.getTotalPages());

            // Return successful response with pagination metadata
            return ResponseEntity.ok(usersPage);

        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve
     * @return ResponseEntity containing the User entity if found, or an error response
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        log.info("Retrieving user by ID: {}", id);
        try {
            User user = userService.getUserById(id);
            log.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error retrieving user by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Updates an existing user's information.
     *
     * @param id   The ID of the user to update
     * @param user The updated User entity
     * @return ResponseEntity containing the updated User entity if successful, or an error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Updating user with ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, user);
            log.info("Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Partially updates an existing user's information.
     *
     * @param id   The ID of the user to update
     * @param user The User entity containing the fields to update
     * @return ResponseEntity containing the updated User entity if successful, or an error response
     */
    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody User user) {
        log.info("Patching user with ID: {}", id);
        try {
            User patchedUser = userService.patchUser(id, user);
            log.info("Successfully patched user with ID: {}", id);
            return ResponseEntity.ok(patchedUser);
        } catch (Exception e) {
            log.error("Error patching user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete
     * @return ResponseEntity with a success message or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.info("Attempting to delete user with ID: {}", id);
        try {
            // Check if user exists
            if (userService.findById(id).isEmpty()) {
                log.warn("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "User not found with ID: " + id));
            }

            userService.deleteUser(id);
            log.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.ok(
                    Collections.singletonMap("message", "User with ID " + id + " successfully deleted"));
        } catch (Exception e) {
            log.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Failed to delete user: " + e.getMessage()));
        }
    }
}

