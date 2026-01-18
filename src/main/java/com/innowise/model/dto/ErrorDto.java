package com.innowise.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for error responses.
 */
@Schema(description = "Error response containing error message")
public record ErrorDto(
                @Schema(description = "Error message describing what went wrong", example = "Invalid credentials provided") String message) {
}
