package com.innowise.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Utility class for generating and validating JWT tokens.
 * Handles token creation, extracting username and roles, and validation.
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;
    private final Counter tokensGeneratedCounter;
    private final Counter tokensValidatedCounter;
    private final Timer tokenGenerationTimer;
    private final Timer tokenValidationTimer;

    /**
     * Constructor initializes signing key and expiration time from application
     * properties.
     *
     * @param secret       JWT secret key
     * @param expirationMs Expiration time in milliseconds
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs,
            Counter tokensGeneratedCounter,
            Counter tokensValidatedCounter,
            Timer tokenGenerationTimer,
            Timer tokenValidationTimer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
        this.tokensGeneratedCounter = tokensGeneratedCounter;
        this.tokensValidatedCounter = tokensValidatedCounter;
        this.tokenGenerationTimer = tokenGenerationTimer;
        this.tokenValidationTimer = tokenValidationTimer;
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param username for User username
     * @param roles    for User Roles
     * @return Signed JWT token
     */
    public String generateToken(String username, Set<String> roles) {
        return tokenGenerationTimer.record(() -> {
            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("roles", roles)
                    .claim("authorities", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            tokensGeneratedCounter.increment();
            return token;
        });
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token JWT token
     * @return Username contained in the token
     */
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Extracts the roles from a JWT token.
     *
     * @param token JWT token
     * @return List of role names
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }

    /**
     * Validates a JWT token by verifying its signature and expiration.
     *
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        return tokenValidationTimer.record(() -> {
            try {
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
                tokensValidatedCounter.increment();
                return true;
            } catch (JwtException e) {
                return false;
            }
        });
    }
}
