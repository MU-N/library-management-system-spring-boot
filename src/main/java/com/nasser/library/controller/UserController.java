package com.nasser.library.controller;

import com.nasser.library.mapper.UserMapper;
import com.nasser.library.model.dto.request.UpdateUserRequest;
import com.nasser.library.model.dto.response.UserListResponse;
import com.nasser.library.model.dto.response.UserResponse;
import com.nasser.library.model.entity.User;
import com.nasser.library.service.UserService;
import com.nasser.library.util.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserMapper userMapper;

    /**
     * Retrieves a list of all users with pagination and sorting.
     *
     * @param page    The page number (zero-based)
     * @param size    The number of items per page
     * @param sortBy  The field to sort by
     * @param sortDir The sort direction (asc or desc)
     * @return ResponseEntity containing a Page of UserListResponse objects
     */
    @GetMapping
    public ResponseEntity<Page<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Retrieving users - Page: {}, Size: {}, Sort: {} {}", page, size, sortBy, sortDir);
        try {


            // Create pageable with validation (consolidates all pagination logic)
            Pageable pageable = ValidationUtils.createPageable(page, size, sortBy, sortDir, log);
            if (pageable == null) {
                return ResponseEntity.badRequest().build();
            }
            // Get paginated users
            Page<User> usersPage = userService.getAllUsers(pageable);
            Page<UserListResponse> responsePage = usersPage.map(userMapper::toListResponse);

            log.info("Successfully retrieved {} users out of {} total users. Page {}/{}",
                    responsePage.getNumberOfElements(),
                    responsePage.getTotalElements(),
                    responsePage.getNumber() + 1,
                    responsePage.getTotalPages());

            // Return successful response with pagination metadata
            return ResponseEntity.ok(responsePage);

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
    public ResponseEntity<UserResponse> getUserById(@RequestParam Long id) {
        log.info("Retrieving user by ID: {}", id);
        try {
            User user = userService.getUserById(id);
            UserResponse userResponse = userMapper.toResponse(user);
            log.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Error retrieving user by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Updates an existing user's information.
     *
     * @param id      The ID of the user to update
     * @param request The UpdateUserRequest containing the updated user information
     * @return ResponseEntity containing the updated User entity if successful, or an error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        try {
            User user = userMapper.toEntity(request);
            User updatedUser = userService.updateUser(id, user);
            UserResponse userResponse = userMapper.toResponse(updatedUser);
            log.info("Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Error updating user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Partially updates an existing user's information.
     *
     * @param id      The ID of the user to update
     * @param request The UpdateUserRequest containing the updated user information
     * @return ResponseEntity containing the updated User entity if successful, or an error response
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Patching user with ID: {}", id);
        try {
            User user = userMapper.toEntity(request);
            User patchedUser = userService.patchUser(id, user);
            UserResponse userResponse = userMapper.toResponse(patchedUser);
            log.info("Successfully patched user with ID: {}", id);
            return ResponseEntity.ok(userResponse);
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

