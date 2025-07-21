package org.example.authservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinTable(name = "user_id")
    private User user;

    private TypeOfToken tokenType;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    private boolean revoked;
}
