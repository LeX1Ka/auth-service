package org.example.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmRequest {
    private UserRegistrationRequest request;
    private String code;
}
