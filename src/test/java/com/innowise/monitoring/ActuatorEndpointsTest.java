package com.innowise.monitoring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Actuator endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActuatorEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpoint_ShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void healthEndpoint_ShouldIncludeDatabaseHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.database").exists())
                .andExpect(jsonPath("$.components.database.status").exists());
    }

    @Test
    void healthEndpoint_ShouldIncludeJwtHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.jwt").exists())
                .andExpect(jsonPath("$.components.jwt.status").exists());
    }

    @Test
    void infoEndpoint_ShouldReturnApplicationInfo() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("InnoAuthService"))
                .andExpect(jsonPath("$.app.version").value("1.0.0"));
    }

    @Test
    void metricsEndpoint_ShouldReturnMetricsList() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray())
                .andExpect(jsonPath("$.names").isNotEmpty());
    }

    @Test
    void prometheusEndpoint_ShouldReturnMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;version=0.0.4;charset=utf-8"));
    }

    @Test
    void prometheusEndpoint_ShouldIncludeHttpMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("http_server_requests")));
    }

    @Test
    void prometheusEndpoint_ShouldIncludeJvmMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("jvm_memory")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("jvm_threads")));
    }

    @Test
    void prometheusEndpoint_ShouldIncludeCommonTags() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("service=\"auth-service\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("version=\"1.0.0\"")));
    }

    @Test
    void envEndpoint_ShouldReturnEnvironmentProperties() throws Exception {
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertySources").isArray());
    }

    @Test
    void loggersEndpoint_ShouldReturnLoggerConfiguration() throws Exception {
        mockMvc.perform(get("/actuator/loggers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loggers").exists());
    }

    @Test
    void specificMetric_ShouldReturnHttpServerRequests() throws Exception {
        mockMvc.perform(get("/actuator/metrics/http.server.requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("http.server.requests"))
                .andExpect(jsonPath("$.measurements").isArray());
    }
}
