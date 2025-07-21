package org.example.authservice.service;

import org.example.authservice.dto.UserLoginRequest;
import org.example.authservice.dto.UserRegistrationRequest;

import javax.naming.AuthenticationException;

public interface UserService {
    void register(UserRegistrationRequest userRegistrationRequest);
    void login(UserLoginRequest userLoginRequest);
    void changeRole(String login, String newRole);

}
