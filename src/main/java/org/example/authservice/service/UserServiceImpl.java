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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Override
    public boolean confirmRegistration(UserRegistrationRequest request, String code) {
        if (!emailVerificationService.verify(request.getEmail(), code)) {
            return false;
        }

        User user = new User();
        user.setLogin(request.getLogin());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.GUEST));
        userRepository.save(user);

        return true;
    }


    @Override
    public void register(UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByLogin(userRegistrationRequest.getLogin()) ||
                userRepository.existsByEmail(userRegistrationRequest.getEmail())) {
            throw new RegistrationException("Такой Email или Login уже существует");
        }

        emailVerificationService.sendVerificationCode(userRegistrationRequest.getEmail());

        System.out.println("Код подтверждения отправлен на: " + userRegistrationRequest.getEmail());
    }

    @Override
    public UserTokenResponse login(UserLoginRequest userLoginRequest) {
        UserTokenResponse userTokenResponse = new UserTokenResponse();
        String login = userLoginRequest.getLogin();
        String password = userLoginRequest.getPassword();

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new LoginException("Неверный логин"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new LoginException("Неверный пароль");
        }
        System.out.println("Вход выполнен для: " + user.getLogin());

        userTokenResponse.setAccessToken(tokenService.generateAccessToken(user).getToken());
        userTokenResponse.setRefreshToken(tokenService.generateRefreshToken(user).getToken());

        return userTokenResponse;
    }

    @Override
    public UserTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String oldRefreshValue = refreshTokenRequest.getRefreshToken();

        Token oldRefresh = tokenRepository.findByToken(oldRefreshValue)
                .orElseThrow(() -> new LoginException("Неверный refresh token"));

        if (oldRefresh.getExpiresAt().isBefore(LocalDateTime.now()) || oldRefresh.isRevoked()) {
            throw new LoginException("Token истек или отозван");
        }

        User user = oldRefresh.getUser();

        oldRefresh.setRevoked(true);
        tokenRepository.save(oldRefresh);

        Token newAccessToken = tokenService.generateAccessToken(user);
        Token newRefreshToken = tokenService.generateRefreshToken(user);

        UserTokenResponse response = new UserTokenResponse();
        response.setAccessToken(newAccessToken.getToken());
        response.setRefreshToken(newRefreshToken.getToken());

        return response;
    }


    @Override
    public void changeRole(String targetLogin, String newRoleName) {
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByLogin(currentLogin)
                .orElseThrow(() -> new IllegalStateException("Текущий пользователь не найден"));

        //Для теста комментировать этот блок 115-117 строки
        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Недостаточно прав: требуется роль ADMIN");
        }

        User targetUser = userRepository.findByLogin(targetLogin)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + targetLogin));

        Role newRole = parseRole(newRoleName);

        targetUser.setRoles(new java.util.HashSet<>(Set.of(newRole)));

        userRepository.save(targetUser);
    }


    private Role parseRole(String roleName) {
        try {
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Недопустимая роль: " + roleName + ". Доступные: ADMIN, PREMIUM_USER, GUEST"
            );
        }
    }


    @Override
    public void logout(String tokenValue) {
        tokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
        });
    }


}
