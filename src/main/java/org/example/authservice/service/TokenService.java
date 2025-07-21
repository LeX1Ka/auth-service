package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.entity.Token;
import org.example.authservice.entity.TypeOfToken;
import org.example.authservice.entity.User;
import org.example.authservice.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token generateAccessToken(User user) {
        Token token = new Token();

        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setTokenType(TypeOfToken.ACCESS_TOKEN);
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setRevoked(false);

        return tokenRepository.save(token);

    }

    public Token generateRefreshToken(User user) {
        Token token = new Token();

        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setTokenType(TypeOfToken.REFRESH_TOKEN);
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);

        return tokenRepository.save(token);
    }

    public void revokeToken(String tokenValue) {
        Token token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> IllegalArgumentException("Token not found"));

        token.setRevoked(true);
        tokenRepository.save(token);
    }

}
