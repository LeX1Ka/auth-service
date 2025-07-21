package org.example.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRegistrationRequest {
    private String login;
    private String email;
    private String password;
}
