package com.nasser.library.controller;

import com.nasser.library.model.dto.request.AuthRequest;
import com.nasser.library.model.dto.request.RegisterRequest;
import com.nasser.library.model.dto.response.AuthResponse;
import com.nasser.library.model.entity.User;
import com.nasser.library.service.JwtService;
import com.nasser.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller - REST endpoints for user authentication
 * <p>
 * This controller handles all authentication-related operations including:
 * - User registration and account creation
 * - User login and JWT token issuance
 * - Token refresh for maintaining sessions
 * - Logout operations (client-side token invalidation)
 * <p>
 * Security Features:
 * - Input validation for all requests
 * - Proper error handling and logging
 * - JWT token generation and management
 * - Authentication state management
 * <p>
 * Endpoints:
 * - POST /api/auth/register - User registration
 * - POST /api/auth/login - User authentication
 * - POST /api/auth/refresh-token - Token renewal
 * - POST /api/auth/logout - Session termination
 */


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    /**
     * User Registration Endpoint
     * <p>
     * Creates a new user account with provided details.
     * Validates input data and returns JWT tokens upon successful registration.
     *
     * @param registerRequest User registration data
     * @param bindingResult   Validation results
     * @return AuthResponse with tokens and user info, or error response
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
        log.info("Registration attempt for email: {}", registerRequest.email());

        // Step 1: Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            log.warn("Registration validation failed for email: {}, errors: {}",
                    registerRequest.email(), errors);
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Step 2: Validate password confirmation
        if (!registerRequest.isPasswordMatching()) {
            log.warn("Password confirmation mismatch for email: {}", registerRequest.email());
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Password and confirmation password do not match")
            );
        }

        try {
            // Step 3: Check if email already exists
            if (userService.existsByEmail(registerRequest.email())) {
                log.warn("Registration failed - email already exists: {}", registerRequest.email());
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Email address is already registered")
                );
            }
            // Step 4: Create new user account
            User newUser = userService.registerUser(registerRequest);
            // Step 5: Generate JWT tokens for immediate login
            String accessToken = jwtService.generateToken(newUser);

            // Step 6: Build response with tokens and user info using record constructor
            AuthResponse response = AuthResponse.of(
                    accessToken,
                    jwtService.getExpirationTime() / 1000, // Convert to seconds
                    newUser.getId(),
                    newUser.getEmail(),
                    newUser.getFirstName(),
                    newUser.getLastName(),
                    newUser.getRole().name(),
                    newUser.getProvider().name(),
                    newUser.getMaxBooksAllowed(),
                    true // isFirstLogin
            );
            log.info("User registered successfully: {} with ID: {}",
                    newUser.getEmail(), newUser.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Registration failed for email: {} - {}",
                    registerRequest.email(), e.getMessage());
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            log.error("Unexpected error during registration for email: {} - {}",
                    registerRequest.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Registration failed. Please try again.")
            );
        }
    }


    /**
     * User Login Endpoint
     * <p>
     * Authenticates user credentials and returns JWT tokens.
     * Validates credentials against stored user data.
     *
     * @param authRequest   User login credentials
     * @param bindingResult Validation results
     * @return AuthResponse with tokens and user info, or error response
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthRequest authRequest,
            BindingResult bindingResult
    ) {
        log.info("Login attempt for email: {}", authRequest.email());

        // Step 1: Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            log.warn("Login validation failed for email: {}, errors: {}",
                    authRequest.email(), errors);
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            // Step 2: Authenticate user credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
            );

            // Step 3: Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = (User) userDetails; // Safe cast since our User implements UserDetails

            // Step 4: Generate JWT tokens
            String accessToken = jwtService.generateToken(userDetails);

            // Step 5: Build response with tokens and user info using record constructor
            AuthResponse response = AuthResponse.of(
                    accessToken,
                    jwtService.getExpirationTime() / 1000, // Convert to seconds
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name(),
                    user.getProvider().name(),
                    user.getMaxBooksAllowed(),
                    false // isFirstLogin
            );
            log.info("User authenticated successfully: {}", user.getEmail());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for email: {} - Invalid credentials",
                    authRequest.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Invalid email or password")
            );
        } catch (Exception e) {
            log.error("Unexpected error during authentication for email: {} - {}",
                    authRequest.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Authentication failed. Please try again.")
            );
        }

    }

}
