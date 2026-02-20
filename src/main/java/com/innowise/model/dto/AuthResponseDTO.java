package com.innowise.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for authentication responses.
 * Contains the JWT token, username, and refresh token.
 */
@Data
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT tokens and user info")
public class AuthResponseDTO {

    @Schema(description = "Authenticated user's ID", example = "1")
    private Long id;

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Authenticated user's username", example = "john_doe")
    private String username;

    @Schema(description = "Authenticated user's email", example = "john@example.com")
    private String email;

    @Schema(description = "Authenticated user's name", example = "John")
    private String name;

    @Schema(description = "Authenticated user's surname", example = "Doe")
    private String surname;

    @Schema(description = "Authenticated user's birth date", example = "1990-01-01")
    private java.time.LocalDate birthDate;

    @Schema(description = "Authenticated user's role", example = "REALM_USER")
    private String role;

    @Schema(description = "Refresh token used to obtain a new access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}
