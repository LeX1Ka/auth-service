package org.example.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.*;
import org.example.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        userService.register(request);
        return ResponseEntity.ok("Код подтверждения отправлен на почту.");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestBody ConfirmRequest confirmRequest) {
        boolean success = userService.confirmRegistration(
                confirmRequest.getRequest(), confirmRequest.getCode());

        if (success) {
            return ResponseEntity.ok("Регистрация подтверждена.");
        } else {
            return ResponseEntity.badRequest().body("Неверный или просроченный код.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenResponse> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @PostMapping("/change-role")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeRole(@RequestBody RoleChangeRequest request) {
        userService.changeRole(request.getLogin(), request.getNewRole());
        return ResponseEntity.ok("Роль успешно изменена.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Отсутствует access-токен");
        }

        String token = authHeader.substring(7);
        userService.logout(token);
        return ResponseEntity.ok("Вы успешно вышли из системы");
    }

}
