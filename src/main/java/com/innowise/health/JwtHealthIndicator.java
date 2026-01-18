package com.innowise.health;

import com.innowise.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Custom health indicator for JWT token generation and validation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHealthIndicator implements HealthIndicator {

    private final JwtUtil jwtUtil;

    @Override
    public Health health() {
        try {
            String testUsername = "health-check-user";
            Set<String> testRoles = Set.of("ROLE_USER");
            String testToken = jwtUtil.generateToken(testUsername, testRoles);

            if (testToken == null || testToken.isEmpty()) {
                return Health.down()
                        .withDetail("jwt", "Token generation")
                        .withDetail("error", "Generated token is null or empty")
                        .build();
            }

            if (!jwtUtil.validateToken(testToken)) {
                return Health.down()
                        .withDetail("jwt", "Token validation")
                        .withDetail("error", "Generated token failed validation")
                        .build();
            }

            String extractedUsername = jwtUtil.getUsername(testToken);
            if (!testUsername.equals(extractedUsername)) {
                return Health.down()
                        .withDetail("jwt", "Token claims")
                        .withDetail("error", "Username mismatch in token claims")
                        .build();
            }

            Map<String, Object> details = new HashMap<>();
            details.put("jwt", "Functioning correctly");
            details.put("token_generation", "OK");
            details.put("token_validation", "OK");
            details.put("claims_extraction", "OK");

            return Health.up().withDetails(details).build();

        } catch (Exception e) {
            log.error("JWT health check failed", e);
            return Health.down()
                    .withDetail("jwt", "Error during health check")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}