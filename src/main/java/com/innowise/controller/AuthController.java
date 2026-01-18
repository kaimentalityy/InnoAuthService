package com.innowise.controller;

import com.innowise.model.dto.AuthResponseDTO;
import com.innowise.model.dto.AuthDto;
import com.innowise.model.dto.ErrorDto;
import com.innowise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for user authentication and token management.
 * <p>
 * Provides endpoints for registering users, logging in, refreshing tokens,
 * and validating JWT access tokens.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

        private final AuthService authService;

        /**
         * Registers a new user with the provided credentials.
         *
         * @param request the registration data containing username and password
         * @return {@link AuthResponseDTO} containing access token, refresh token, and
         *         username
         */
        @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                        @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
        })
        public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid AuthDto request) {
                AuthResponseDTO resp = authService.register(request);
                return ResponseEntity.ok(resp);
        }

        /**
         * Authenticates an existing user and returns tokens.
         *
         * @param request the login data containing username and password
         * @return {@link AuthResponseDTO} containing access token, refresh token, and
         *         username
         */
        @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Authenticate user", description = "Authenticates a user and returns JWT tokens")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
        })
        public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthDto request) {
                AuthResponseDTO resp = authService.login(request);
                return ResponseEntity.ok(resp);
        }

        /**
         * Refreshes the access token using a valid refresh token.
         *
         * @param refreshToken the refresh token
         * @return a map containing the new access token and the same refresh token
         */
        @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid refresh token", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
        })
        public ResponseEntity<Map<String, String>> refresh(
                        @RequestParam(name = "refreshToken", required = true) String refreshToken) {
                Map<String, String> map = authService.refresh(refreshToken);
                return ResponseEntity.ok(map);
        }

        /**
         * Validates a JWT access token.
         *
         * @param token the JWT access token to validate
         * @return a map with a single entry "valid" set to true if the token is valid,
         *         false otherwise
         */
        @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Validate token", description = "Validates a JWT access token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token validation result", content = @Content(schema = @Schema(implementation = Map.class)))
        })
        public ResponseEntity<Map<String, Boolean>> validate(
                        @RequestParam(name = "token", required = true) String token) {
                boolean isValid = authService.validateToken(token);
                return ResponseEntity.ok(Map.of("valid", isValid));
        }
}
