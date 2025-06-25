package com.nasser.library.filter;


import com.nasser.library.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * JWT Authentication Filter - Request Interceptor for JWT Token Validation
 * <p>
 * This filter extends OncePerRequestFilter to ensure it's executed only once per request.
 * It acts as a gatekeeper for all incoming HTTP requests.
 * <p>
 * Filter Responsibilities:
 * - Intercepts every HTTP request before it reaches controllers
 * - Extracts JWT tokens from Authorization headers
 * - Validates token authenticity and expiration
 * - Loads user details and sets authentication context
 * - Allows requests to continue through the filter chain
 * <p>
 * Security Flow:
 * 1. Check for Authorization header
 * 2. Extract and validate JWT token
 * 3. Authenticate user and set security context
 * 4. Pass request to next filter in chain
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Authorization header name constant
    private static final String AUTHORIZATION_HEADER = "Authorization";

    // Bearer token prefix constant
    private static final String BEARER_PREFIX = "Bearer ";

    // Length of "Bearer " prefix
    private static final int BEARER_PREFIX_LENGTH = 7;

    /**
     * Core filter method that processes every HTTP request
     * Implements the complete JWT authentication flow
     *
     * @param request     HTTP request object
     * @param response    HTTP response object
     * @param filterChain Chain of filters to continue processing
     * @throws ServletException if servlet-related error occurs
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {


        // Step 1: Check if this is a public endpoint that doesn't need authentication
        if (isPublicEndpoint(request)) {
            log.debug("Public endpoint accessed: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2: Extract Authorization header from request
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Step 3: Validate header format and extract token
        if (!isValidAuthorizationHeader(authHeader)) {
            log.debug("No valid Authorization header found for request: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Step 4: Extract JWT token from header (remove "Bearer " prefix)
        final String jwt = authHeader.substring(BEARER_PREFIX_LENGTH);

        try {
            // Step 5: Extract username from JWT token
            final String userEmail = jwtService.extractUsername(jwt);

            // Step 6: Check if user is not already authenticated in current request
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && existingAuth == null) {
                // Step 7: Load user details from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Step 9: Create authentication token for Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,                    // Principal (user details)
                            null,                          // Credentials (not needed for JWT)
                            userDetails.getAuthorities()   // User roles/authorities
                    );

                    // Step 10: Set additional authentication details
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Step 11: Set authentication in Spring Security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Successfully authenticated user: {} for request: {}", userEmail, request.getRequestURI());

                } else {
                    log.warn("Invalid JWT token for user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            // Step 12: Handle token parsing/validation errors
            log.error("Cannot set user authentication for request: {} - Error: {}",
                    request.getRequestURI(), e.getMessage());

            // Clear any partial authentication
            SecurityContextHolder.clearContext();
        }

        // Step 13: Continue with the filter chain regardless of authentication result
        filterChain.doFilter(request, response);

    }


    /**
     * Determines if the current request endpoint is public (doesn't require authentication)
     * Public endpoints include login, register, and other authentication-related paths
     *
     * @param request HTTP request
     * @return true if endpoint is public, false if authentication required
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Define public endpoints that don't require authentication
        return requestPath.startsWith("/api/auth/") ||           // Authentication endpoints
                requestPath.startsWith("/api/public/") ||         // Explicitly public endpoints
                requestPath.equals("/error") ||                   // Error pages
                requestPath.startsWith("/actuator/health");     // Health check
    }

    /**
     * Validates if the Authorization header has the correct format
     * Must start with "Bearer " prefix
     *
     * @param authHeader Authorization header value
     * @return true if header is valid, false otherwise
     */
    private boolean isValidAuthorizationHeader(String authHeader) {
        return authHeader != null &&
                authHeader.startsWith(BEARER_PREFIX) &&
                authHeader.length() > BEARER_PREFIX_LENGTH;
    }

    /**
     * Determines whether this filter should be applied to the given request
     * Can be overridden to exclude certain paths from JWT processing
     *
     * @param request HTTP request
     * @return false to skip this filter, true to apply it
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filter for static resources
        String path = request.getRequestURI();
        return path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/favicon.ico");
    }
}
