package com.nasser.library.service;

import com.nasser.library.model.dto.request.RegisterRequest;
import com.nasser.library.model.entity.AuthProvider;
import com.nasser.library.model.entity.Role;
import com.nasser.library.model.entity.User;
import com.nasser.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User save(User newUser) {
        return userRepository.save(newUser);
    }

    /**
     * Spring Security UserDetailsService implementation
     * This method is called by Spring Security during authentication process
     *
     * @param username The username (email in our case) to authenticate
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user by username: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", username);
                    return new UsernameNotFoundException("User not found with email: " + username);
                });

        log.debug("Successfully loaded user: {} with role: {}", user.getEmail(), user.getRole());

        // Return the User entity which implements UserDetails
        return user;
    }

    /**
     * Registers a new user using RegisterRequest record
     * Validates uniqueness, encrypts password, and creates user account
     *
     * @param registerRequest Complete registration data as a record
     * @return Created user entity
     * @throws IllegalArgumentException if email already exists or validation fails
     */
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.email());

        // Step 1: Validate email uniqueness
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            log.warn("Registration failed - email already exists: {}", registerRequest.email());
            throw new IllegalArgumentException("Email already exists: " + registerRequest.email());
        }

        // Step 2: Validate password confirmation (additional safety check)
        if (!registerRequest.isPasswordMatching()) {
            log.warn("Registration failed - password confirmation mismatch for email: {}", registerRequest.email());
            throw new IllegalArgumentException("Password and confirmation password do not match");
        }

        // Step 3: Create new user entity using record data
        User user = User.builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))  // Encrypt password
                .phone(registerRequest.phone())                                // Optional phone number
                .role(Role.MEMBER)                                            // Default to MEMBER role
                .provider(AuthProvider.LOCAL)                                 // Local registration
                .maxBooksAllowed(5)                                          // Default borrowing limit for members
                .build();

        // Step 4: Save user to database
        User savedUser = userRepository.save(user);

        log.info("Successfully registered user: {} with ID: {}", savedUser.getEmail(), savedUser.getId());

        return savedUser;
    }

    /**
     * Validates user credentials during login
     * Checks if provided password matches stored encrypted password
     *
     * @param email       User's email
     * @param rawPassword Plain text password to validate
     * @return true if credentials are valid, false otherwise
     */
    public boolean validateUserCredentials(String email, String rawPassword) {
        log.debug("Validating credentials for user: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.warn("Validation failed - user not found: {}", email);
            return false;
        }

        User user = userOpt.get();

        // Check if user has a password (OAuth2 users might not have one)
        if (user.getPassword() == null) {
            log.warn("Validation failed - user has no password (OAuth2 user?): {}", email);
            return false;
        }

        // Use BCrypt to validate password
        boolean isValid = passwordEncoder.matches(rawPassword, user.getPassword());

        if (isValid) {
            log.debug("Password validation successful for user: {}", email);
        } else {
            log.warn("Password validation failed for user: {}", email);
        }

        return isValid;
    }

    /**
     * Finds a user by email address
     *
     * @param email User's email address
     * @return Optional containing user if found, empty otherwise
     */
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by ID
     *
     * @param id User's unique identifier
     * @return Optional containing user if found, empty otherwise
     */
    public Optional<User> findById(Long id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Updates user's password
     * Encrypts the new password before saving
     *
     * @param userId         User's ID
     * @param newRawPassword New plain text password
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void updatePassword(Long userId, String newRawPassword) {
        log.info("Updating password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Encrypt and set new password
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user ID: {}", userId);
    }

    /**
     * Updates user's role
     * Used for promoting/demoting users
     *
     * @param userId  User's ID
     * @param newRole New role to assign
     * @throws IllegalArgumentException if user not found
     */
    @Transactional
    public void updateUserRole(Long userId, Role newRole) {
        log.info("Updating role for user ID: {} to: {}", userId, newRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Role oldRole = user.getRole();
        user.setRole(newRole);

        // Adjust book borrowing limits based on role
        if (newRole == Role.MEMBER) {
            user.setMaxBooksAllowed(5);
        } else {
            user.setMaxBooksAllowed(10);
        }

        userRepository.save(user);

        log.info("Role updated for user ID: {} from {} to {}", userId, oldRole, newRole);
    }

    /**
     * Checks if an email address is already registered
     * Used for registration validation
     *
     * @param email Email address to check
     * @return true if email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        log.debug("Email existence check for {}: {}", email, exists);
        return exists;
    }

    /**
     * Creates or updates a user from OAuth2 provider
     * Used for social login integration
     *
     * @param email     User's email
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param provider  OAuth2 provider (GOOGLE, GITHUB, etc.)
     * @return Created or updated user
     */
    @Transactional
    public User createOrUpdateOAuth2User(String email, String firstName,
                                         String lastName, AuthProvider provider) {
        log.info("Creating/updating OAuth2 user: {} from provider: {}", email, provider);
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // Update existing user's provider info if needed
            User user = existingUser.get();
            if (user.getProvider() != provider) {
                user.setProvider(provider);
                userRepository.save(user);
            }
            log.debug("Updated existing OAuth2 user: {}", email);
            return user;
        } else {
            // Create new OAuth2 user
            User newUser = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(null)  // OAuth2 users don't have passwords
                    .role(Role.MEMBER)
                    .provider(provider)
                    .maxBooksAllowed(5)
                    .build();

            User savedUser = userRepository.save(newUser);
            log.info("Created new OAuth2 user: {} with ID: {}", email, savedUser.getId());
            return savedUser;
        }
    }

}
