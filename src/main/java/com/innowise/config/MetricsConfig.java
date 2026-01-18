package com.innowise.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for custom business metrics using Micrometer.
 * These metrics will be exposed via Prometheus and visualized in Grafana.
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    /**
     * Customizes the MeterRegistry to add common tags to all metrics.
     * These tags help identify metrics in Prometheus/Grafana.
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(
                        "application", "inno-auth-service",
                        "service", "auth-service");
    }

    /**
     * Enables @Timed annotation support for method execution timing.
     * This allows precise timing of specific methods.
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Counter for tracking authentication attempts
     */
    @Bean
    public Counter authenticationAttemptsCounter(MeterRegistry registry) {
        return Counter.builder("auth.attempts.total")
                .description("Total number of authentication attempts")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Counter for tracking successful authentications
     */
    @Bean
    public Counter authenticationSuccessCounter(MeterRegistry registry) {
        return Counter.builder("auth.success.total")
                .description("Total number of successful authentications")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Counter for tracking failed authentications
     */
    @Bean
    public Counter authenticationFailureCounter(MeterRegistry registry) {
        return Counter.builder("auth.failure.total")
                .description("Total number of failed authentications")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Timer for tracking authentication duration
     */
    @Bean
    public Timer authenticationTimer(MeterRegistry registry) {
        return Timer.builder("auth.authentication.duration")
                .description("Time taken for authentication operations")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Timer for tracking token generation duration
     */
    @Bean
    public Timer tokenGenerationTimer(MeterRegistry registry) {
        return Timer.builder("auth.token.generation.duration")
                .description("Time taken for JWT token generation")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Timer for tracking token validation duration
     */
    @Bean
    public Timer tokenValidationTimer(MeterRegistry registry) {
        return Timer.builder("auth.token.validation.duration")
                .description("Time taken for JWT token validation")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Counter for tracking token generation
     */
    @Bean
    public Counter tokensGeneratedCounter(MeterRegistry registry) {
        return Counter.builder("auth.tokens.generated.total")
                .description("Total number of JWT tokens generated")
                .tag("service", "auth-service")
                .register(registry);
    }

    /**
     * Counter for tracking token validation
     */
    @Bean
    public Counter tokensValidatedCounter(MeterRegistry registry) {
        return Counter.builder("auth.tokens.validated.total")
                .description("Total number of JWT tokens validated")
                .tag("service", "auth-service")
                .register(registry);
    }
}
