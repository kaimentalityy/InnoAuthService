package com.innowise.service;

import com.innowise.model.dto.AuthResponseDTO;
import com.innowise.model.dto.AuthDto;
import com.innowise.model.dto.LoginDto;

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
     * Authenticates a user and returns tokens.
     *
     * @param request login data
     * @return auth response with tokens
     */
    AuthResponseDTO login(LoginDto request);
}
