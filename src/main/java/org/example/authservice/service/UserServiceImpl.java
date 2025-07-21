package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.UserRegistrationRequest;
import org.example.authservice.entity.User;
import org.example.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void register(UserRegistrationRequest userRegistrationRequest) {
        System.out.println("Registering user: " + userRegistrationRequest.getLogin());

        User user = new User();
        if (userRepository.findByLogin(userRegistrationRequest.getLogin().isPresent())
    }
}
