package org.example.authservice.dto;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String login;
    private String password;
}
