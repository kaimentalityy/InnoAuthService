package com.innowise.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for login requests.
 */
@Schema(description = "Login request containing username and password")
public record LoginDto(
        @NotBlank(message = "Username is required") @Schema(description = "User's unique username", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED) String username,

        @NotBlank(message = "Password is required") @Schema(description = "User's password", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED) String password) {
}
