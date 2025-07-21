package org.example.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeRequest {
    private String login;
    private String newRole;
}
