package com.innowise.service.impl;

import com.innowise.client.UserClient;
import com.innowise.exception.EntityAlreadyExistsException;
import com.innowise.model.dto.*;
import com.innowise.model.entity.AuthUser;
import com.innowise.repository.UserRepository;
import com.innowise.service.AuthService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserClient userClient;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final Counter authenticationAttemptsCounter;
    private final Counter authenticationSuccessCounter;
    private final Counter authenticationFailureCounter;
    private final Timer authenticationTimer;

    @Override
    @Transactional
    public AuthResponseDTO register(AuthDto request) {
        authenticationAttemptsCounter.increment();

        return authenticationTimer.record(() -> {
            if (userRepository.existsByUsername(request.username())) {
                authenticationFailureCounter.increment();
                throw new EntityAlreadyExistsException("User already exists");
            }

            UserRegisterDto userRequest = new UserRegisterDto(
                    request.name(), request.surname(), request.birthDate(), request.email());
            userClient.createUserInUserService(userRequest);

            String keycloakId = null;
            try {
                UserRepresentation user = getUserRepresentation(request);

                try (Response response = keycloak.realm(realm).users().create(user)) {
                    if (response.getStatus() != 201) {
                        throw new RuntimeException("Keycloak error: " + response.getStatusInfo().getReasonPhrase());
                    }
                    keycloakId = CreatedResponseUtil.getCreatedId(response);
                }

                CredentialRepresentation passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                passwordCred.setValue(request.password());
                keycloak.realm(realm).users().get(keycloakId).resetPassword(passwordCred);

                try {
                    RoleRepresentation userRole = keycloak.realm(realm).roles().get("user").toRepresentation();
                    keycloak.realm(realm).users().get(keycloakId).roles().realmLevel()
                            .add(Collections.singletonList(userRole));
                    log.info("Successfully assigned user role to user {}", request.username());
                } catch (jakarta.ws.rs.WebApplicationException e) {
                    String errorBody = e.getResponse().readEntity(String.class);
                    log.error("Failed to assign user role to user {}: Status={}, Error={}",
                            request.username(), e.getResponse().getStatus(), errorBody, e);
                } catch (Exception e) {
                    log.error("Failed to assign user role to user {}: {}", request.username(), e.getMessage(), e);
                }

                AuthUser authUser = new AuthUser();
                authUser.setKeycloakId(keycloakId);
                authUser.setUsername(request.username());
                authUser.setEmail(request.email());
                userRepository.save(authUser);

                authenticationSuccessCounter.increment();

                return login(new LoginDto(request.username(), request.password()));

            } catch (Exception e) {
                authenticationFailureCounter.increment();
                log.error("Failed to sync with Keycloak for user {}", request.username(), e);
                log.info("Rolling back User Service profile for email: {}", request.email());

                try {
                    if (keycloakId != null) {
                        keycloak.realm(realm).users().get(keycloakId).remove();
                    }
                } catch (Exception keycloakRollbackException) {
                    log.error("Failed to rollback Keycloak user", keycloakRollbackException);
                }

                userClient.deleteUserInUserService(request.email());
                throw new RuntimeException("Identity sync failed, rollback triggered", e);
            }
        });
    }

    @Override
    public AuthResponseDTO login(LoginDto request) {
        authenticationAttemptsCounter.increment();

        return authenticationTimer.record(() -> {
            log.debug("Attempting login for user: {}", request.username());

            try (Keycloak userKeycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(request.username())
                    .password(request.password())
                    .grantType(OAuth2Constants.PASSWORD)
                    .build()) {

                org.keycloak.representations.AccessTokenResponse tokenResponse = userKeycloak.tokenManager()
                        .getAccessToken();

                if (tokenResponse == null || tokenResponse.getToken() == null) {
                    log.error("Failed to obtain token for user: {}", request.username());
                    throw new RuntimeException("Failed to get token from Keycloak");
                }

                KeycloakTokenResponse customResponse = new KeycloakTokenResponse(
                        tokenResponse.getToken(),
                        tokenResponse.getExpiresIn(),
                        tokenResponse.getRefreshExpiresIn(),
                        tokenResponse.getRefreshToken(),
                        tokenResponse.getTokenType(),
                        tokenResponse.getNotBeforePolicy(),
                        tokenResponse.getSessionState(),
                        tokenResponse.getScope());

                return processSuccessfulAuthentication(request.username(), customResponse);

            } catch (WebApplicationException e) {
                String errorBody = e.getResponse().readEntity(String.class);
                log.error("Keycloak authentication failed for user {}. Status: {}, Error: {}",
                        request.username(), e.getResponse().getStatus(), errorBody);
                authenticationFailureCounter.increment();

                if (e.getResponse().getStatus() == 401) {
                    throw new RuntimeException("Invalid username or password", e);
                }
                throw new RuntimeException("Authentication server error: " + errorBody, e);
            } catch (Exception e) {
                log.error("Unexpected authentication error for user {}", request.username(), e);
                authenticationFailureCounter.increment();
                throw new RuntimeException("Authentication server error: " + e.getMessage(), e);
            }
        });
    }

    private AuthResponseDTO processSuccessfulAuthentication(String username, KeycloakTokenResponse tokenResponse) {
        AuthUser authUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in local database"));

        UserDto userProfile = userClient.fetchUserProfile(authUser.getEmail());

        authenticationSuccessCounter.increment();

        return new AuthResponseDTO(
                authUser.getId(),
                tokenResponse.accessToken(),
                authUser.getUsername(),
                authUser.getEmail(),
                userProfile != null ? userProfile.name() : null,
                userProfile != null ? userProfile.surname() : null,
                userProfile != null ? userProfile.birthDate() : null,
                null,
                tokenResponse.refreshToken());
    }

    private static UserRepresentation getUserRepresentation(AuthDto request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.name());
        user.setLastName(request.surname());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRequiredActions(Collections.emptyList());
        return user;
    }
}
