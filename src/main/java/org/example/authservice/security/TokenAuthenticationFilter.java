package org.example.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.authservice.entity.Token;
import org.example.authservice.entity.TypeOfToken;
import org.example.authservice.entity.User;
import org.example.authservice.repository.TokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/register",
            "/auth/confirm",
            "/auth/login",
            "/auth/refresh"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("=== TokenAuthenticationFilter START ===");
        System.out.println("PATH: " + path);

        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            if (path.equals(publicEndpoint)) {
                System.out.println("Public endpoint, skipping token auth");
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenValue = authHeader.substring(7);
        Token token = tokenRepository.findByToken(tokenValue).orElse(null);

        if (token == null ||
                token.isRevoked() ||
                token.getExpiresAt().isBefore(LocalDateTime.now()) ||
                token.getTokenType() != TypeOfToken.ACCESS_TOKEN) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = token.getUser();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getLogin(),
                        null,
                        user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                                .collect(Collectors.toList())
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
