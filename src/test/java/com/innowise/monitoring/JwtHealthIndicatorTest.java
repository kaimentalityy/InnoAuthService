package com.innowise.monitoring;

import com.innowise.health.JwtHealthIndicator;
import com.innowise.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtHealthIndicator
 */
@ExtendWith(MockitoExtension.class)
class JwtHealthIndicatorTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtHealthIndicator healthIndicator;

    @Test
    void health_WhenJwtIsHealthy_ShouldReturnUp() {
        String testToken = "test.jwt.token";
        when(jwtUtil.generateToken(anyString(), anySet())).thenReturn(testToken);
        when(jwtUtil.validateToken(testToken)).thenReturn(true);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertTrue(health.getDetails().containsKey("jwt"));
        assertEquals("Operational", health.getDetails().get("jwt"));
    }

    @Test
    void health_WhenJwtFails_ShouldReturnDown() {
        when(jwtUtil.generateToken(anyString(), anySet())).thenThrow(new RuntimeException("JWT generation failed"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().containsKey("error"));
    }
}