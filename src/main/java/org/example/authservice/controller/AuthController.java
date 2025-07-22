package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.ConfirmRequest;
import org.example.authservice.dto.UserRegistrationRequest;
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
        boolean success = userService.confirmRegistration(confirmRequest.getRequest(), confirmRequest.getCode());

        if (success) {
            return ResponseEntity.ok("Регистрация подтверждена.");
        } else {
            return ResponseEntity.badRequest().body("Неверный или просроченный код.");
        }
    }
}
