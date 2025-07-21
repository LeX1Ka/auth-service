package org.example.authservice.service;

import org.example.authservice.dto.UserRegistrationRequest;

public interface UserService {
    void register(UserRegistrationRequest userRegistrationRequest);
}
