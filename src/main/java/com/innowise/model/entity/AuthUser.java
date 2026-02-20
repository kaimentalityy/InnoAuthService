package com.innowise.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing an application user reference.
 * The actual credentials and primary identity are managed by Keycloak.
 */
@Entity
@Table(name = "auth_users")
@Data
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String keycloakId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "email")
    private String email;
}