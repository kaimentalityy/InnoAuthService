package com.innowise.controller;

import com.innowise.model.dto.AuthResponseDTO;
import com.innowise.model.dto.AuthDto;
import com.innowise.model.dto.LoginDto;
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
import org.springframework.web.bind.annotation.RestController;

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
         * Authenticates a user and returns tokens.
         *
         * @param request the login data containing username and password
         * @return {@link AuthResponseDTO} containing access token and user info
         */
        @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
        })
        public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginDto request) {
                AuthResponseDTO resp = authService.login(request);
                return ResponseEntity.ok(resp);
        }
}
