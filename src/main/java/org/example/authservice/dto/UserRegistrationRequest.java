package org.example.authservice.dto;

import lombok.Getter;

@Getter
public class UserRegistrationRequest {
    private String login;
    private String email;
    private String password;
}
