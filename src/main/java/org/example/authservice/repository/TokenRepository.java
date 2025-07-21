package org.example.authservice.repository;

import org.example.authservice.entity.Token;
import org.example.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    Optional<Token> findByToken(String token);
    boolean existsByToken(String token);
    List<Token> findAllValidTokensByUser(User user);

}
