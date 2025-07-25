package org.example.authservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserTokenResponse {
    private String accessToken;
    private String refreshToken;
}
