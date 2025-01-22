package org.wfp.authservice.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CreateUserDTO {
    private String username;
    private String email;
    private String password;
    private boolean active;
    private Set<Long> roles;
}

