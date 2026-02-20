package com.innowise.service;

import com.innowise.model.dto.AuthResponseDTO;
import com.innowise.model.dto.AuthDto;
import com.innowise.model.dto.LoginDto;

import java.util.Map;

/**
 * Handles user authentication and token management.
 */
public interface AuthService {

    /**
     * Registers a new user and returns tokens.
     *
     * @param request registration data
     * @return auth response with tokens
     */
    AuthResponseDTO register(AuthDto request);

    /**
     * Logs in an existing user.
     *
     * @param request login data
     * @return auth response with tokens
     */
    AuthResponseDTO login(LoginDto request);

    /**
     * Refreshes the access token.
     *
     * @param refreshToken existing refresh token
     * @return new access and refresh tokens
     */
    Map<String, String> refresh(String refreshToken);

    /**
     * Validates a JWT access token.
     *
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);
}
