package com.nasser.library.config;

import com.nasser.library.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration - Central security setup for the application
 * <p>
 * This configuration class sets up Spring Security with JWT-based authentication.
 * It defines the security policies, authentication mechanisms, and access controls.
 * <p>
 * Key Components Configured:
 * - Security Filter Chain: Defines URL access rules and authentication requirements
 * - Authentication Provider: Handles user credential validation
 * - JWT Filter Integration: Adds custom JWT filter to the security chain
 * - CORS Configuration: Enables cross-origin requests for frontend integration
 * - Session Management: Configures stateless session policy for JWT
 * <p>
 * Security Features:
 * - Stateless authentication using JWT tokens
 * - Role-based access control (RBAC)
 * - CSRF protection disabled (not needed for stateless APIs)
 * - Method-level security annotations enabled
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize, @PostAuthorize annotations
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Main Security Filter Chain Configuration
     * <p>
     * This method configures the core security policies for the application:
     * - URL-based access controls
     * - Authentication requirements
     * - Session management
     * - Filter chain setup
     *
     * @param http HttpSecurity configuration object
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection (not needed for stateless APIs)
                .csrf(AbstractHttpConfigurer::disable)
                // Configure URL-based authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/v1/auth/**").permitAll()           // Authentication endpoints
                        .requestMatchers("/api/v1/public/**").permitAll()         // Public API endpoints
                        .requestMatchers("/error").permitAll()                 // Error pages
                        .requestMatchers("/actuator/health").permitAll()       // Health checks

                        // Public read access to books (browsing without authentication)
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/authors/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        // Librarian and Admin access
                        .requestMatchers(HttpMethod.POST, "/api/v1/books/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/books/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers("/api/v1/borrowing/manage/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        // Member access (authenticated users)
                        .requestMatchers("/api/v1/borrowing/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                        .requestMatchers("/api/v1/profile/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Configure session management as stateless (no server-side sessions)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Set the authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before the standard username/password authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();

    }

    /**
     * Authentication Provider Configuration
     * <p>
     * Sets up the authentication provider that Spring Security will use
     * to validate user credentials during login.
     * <p>
     * Components:
     * - UserDetailsService: Loads user details from database
     * - PasswordEncoder: Validates encrypted passwords
     *
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Set the service that loads user details
        authProvider.setUserDetailsService(userDetailsService);

        // Set the password encoder for password validation
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    /**
     * Authentication Manager Configuration
     * <p>
     * Provides the authentication manager that handles the authentication process.
     * This is used by the authentication controllers for login operations.
     *
     * @param config Authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
