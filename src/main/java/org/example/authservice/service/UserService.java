package org.example.authservice.service;

import org.example.authservice.dto.UserRegistrationRequest;

import javax.naming.AuthenticationException;

public interface UserService {
    void register(UserRegistrationRequest userRegistrationRequest);
}
