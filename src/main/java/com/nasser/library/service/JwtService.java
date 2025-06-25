package com.nasser.library.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * JWT Service - Core component for JWT token management
 * <p>
 * This service handles:
 * - JWT token generation (access and refresh tokens)
 * - Token validation and signature verification
 * - Claims extraction from tokens
 * - Token expiration management
 * <p>
 * Key Security Features:
 * - Uses HMAC SHA-256 for token signing
 * - Configurable token expiration times
 * - Secure key generation and management
 */
@Service
public class JwtService {

    // Secret key for signing JWT tokens
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    // Access token expiration time
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;



    /**
     * Extracts the username (subject) from the JWT token
     *
     * @param token JWT token string
     * @return username/email from the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any claim from the JWT token
     * Uses function interface for flexible claim extraction
     *
     * @param token          JWT token string
     * @param claimsResolver Function to resolve specific claim
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a standard JWT access token for authenticated user
     * Contains user details and has shorter expiration time
     *
     * @param userDetails Spring Security UserDetails object
     * @return Generated JWT access token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates JWT token with additional custom claims
     * Allows adding extra information to the token payload
     *
     * @param extraClaims Additional claims to include in token
     * @param userDetails Spring Security UserDetails object
     * @return Generated JWT token with custom claims
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }


    /**
     * Core method that builds JWT tokens
     * Creates the token structure with header, payload, and signature
     *
     * @param extraClaims Additional claims to include
     * @param userDetails User information
     * @param expiration  Token expiration time in milliseconds
     * @return Completed JWT token string
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        // Current time for issued at claim
        Date now = new Date(System.currentTimeMillis());

        // Expiration time calculation
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);

        return Jwts
                .builder()
                .setClaims(extraClaims)              // Custom claims
                .setSubject(userDetails.getUsername()) // Username as subject
                .setIssuedAt(now)                     // When token was created
                .setExpiration(expirationDate)        // When token expires
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact();                           // Build the final token string
    }


    /**
     * Validates if the token is valid for the given user
     * Checks both username match and token expiration
     *
     * @param token       JWT token to validate
     * @param userDetails User details to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the token has expired
     * Compares token expiration with current time
     *
     * @param token JWT token to check
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token
     *
     * @param token JWT token
     * @return Token expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the JWT token
     * Parses and validates the token signature
     *
     * @param token JWT token string
     * @return Claims object containing all token data
     * @throws JwtException if token is invalid or expired
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())  // Set the signing key for validation
                .build()
                .parseClaimsJws(token)          // Parse and validate the token
                .getBody();                     // Extract the claims
    }

    /**
     * Generates the signing key from the secret key string
     * Converts base64 encoded secret to SecretKey object
     *
     * @return SecretKey for signing and validating tokens
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Utility method to get token expiration time
     * Useful for client-side token management
     *
     * @return Token expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }


}
