package org.wfp.authservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
}

