package com.innowise.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for authentication requests.
 * Contains user registration and login credentials.
 */
@Schema(description = "Authentication request containing user credentials")
public record AuthDto(
                @NotBlank(message = "Username is required") @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters") @Schema(description = "User's unique username", example = "john_doe", required = true) String username,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be at least 8 characters long") @Schema(description = "User's password (min 8 characters)", example = "securePassword123", required = true) String password,

                @NotBlank(message = "Name is required") @Size(max = 50, message = "Name must be less than 50 characters") @Schema(description = "User's first name", example = "John", required = true) String name,

                @NotBlank(message = "Surname is required") @Size(max = 50, message = "Surname must be less than 50 characters") @Schema(description = "User's last name", example = "Doe", required = true) String surname,

                @NotBlank(message = "Email is required") @Email(message = "Email should be valid") @Schema(description = "User's email address", example = "john.doe@example.com", required = true) String email,

                @NotNull(message = "Birth date is required") @Past(message = "Birth date must be in the past") @Schema(description = "User's date of birth (YYYY-MM-DD)", example = "1990-01-01", required = true) LocalDate birthDate,

                @Schema(description = "User's role (optional, defaults to ROLE_USER)", example = "ROLE_ADMIN") String role) {
}
