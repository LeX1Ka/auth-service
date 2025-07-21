package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.RefreshTokenRequest;
import org.example.authservice.dto.UserLoginRequest;
import org.example.authservice.dto.UserRegistrationRequest;
import org.example.authservice.dto.UserTokenResponse;
import org.example.authservice.entity.Token;
import org.example.authservice.entity.User;
import org.example.authservice.exception.LoginException;
import org.example.authservice.exception.RegistrationException;
import org.example.authservice.repository.TokenRepository;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void register(UserRegistrationRequest userRegistrationRequest) {
        System.out.println("Registering user: " + userRegistrationRequest.getLogin());

        if (userRepository.existsByLogin(userRegistrationRequest.getLogin()) || userRepository.existsByEmail(userRegistrationRequest.getEmail())) {
            throw new RegistrationException("Email or login already exist");
        }
        User user = new User();
        user.setLogin(userRegistrationRequest.getLogin());
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));
        user.setRoles(Set.of(Role.ADMIN));
        userRepository.save(user);

    }

    @Override
    public UserTokenResponse login(UserLoginRequest userLoginRequest) {
        UserTokenResponse userTokenResponse = new UserTokenResponse();
        String login = userLoginRequest.getLogin();
        String password = userLoginRequest.getPassword();

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new LoginException("Invalid login"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new LoginException("Invalid password");
        }
        System.out.println("Login successful for user: " + user.getLogin());

        userTokenResponse.setAccessToken(tokenService.generateAccessToken(user).getId().toString());
        userTokenResponse.setRefreshToken(tokenService.generateRefreshToken(user).getToken());

        return userTokenResponse;
    }

    @Override
    public UserTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        Token token = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new LoginException("Ivalid refresh token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.isRevoked()) {
            throw new LoginException("Token is expired or revoked");
        }

        User user = token.getUser();

        Token newAccessToken = tokenService.generateAccessToken(user);
        Token newRefreshToken = tokenService.generateRefreshToken(user);

        UserTokenResponse userTokenResponse = new UserTokenResponse();
        userTokenResponse.setAccessToken(newAccessToken.getToken());
        userTokenResponse.setRefreshToken(newRefreshToken.getToken());

        return userTokenResponse;
    }

    @Override
    public void changeRole(String login, String newRole) {

    }
}
