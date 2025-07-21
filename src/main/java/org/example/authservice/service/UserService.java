package org.example.authservice.service;

import org.example.authservice.dto.RefreshTokenRequest;
import org.example.authservice.dto.UserLoginRequest;
import org.example.authservice.dto.UserRegistrationRequest;
import org.example.authservice.dto.UserTokenResponse;


public interface UserService {
    void register(UserRegistrationRequest userRegistrationRequest);
    UserTokenResponse login(UserLoginRequest userLoginRequest);
    UserTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void changeRole(String login, String newRole);
}
