package org.wfp.authservice.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wfp.authservice.repository.RoleRepository;

import java.util.List;

@Service
public class RoleValidationService {

    @Autowired
    private RoleRepository roleRepository;

    @Getter
    private final List<String> allowedRoles = List.of("FARMER", "BUYER", "SUPPLIER");

    public boolean isAllowedRole(String roleName) {
        return allowedRoles.contains(roleName);
    }

}

