package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.UserLoginRequest;
import org.example.authservice.dto.UserRegistrationRequest;
import org.example.authservice.entity.User;
import org.example.authservice.exception.LoginException;
import org.example.authservice.exception.RegistrationException;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
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
    public void login(UserLoginRequest userLoginRequest) {
        String login = userLoginRequest.getLogin();
        String password = userLoginRequest.getPassword();

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new LoginException("Invalid login"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new LoginException("Invalid password");
        }

        System.out.println("Login successful for user: " + user.getLogin());
    }

    @Override
    public void changeRole(String login, String newRole) {

    }
}
