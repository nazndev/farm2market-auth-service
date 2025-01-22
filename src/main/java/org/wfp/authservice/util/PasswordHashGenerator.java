package org.wfp.authservice.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        String plainPassword = "nazim";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Hashed Password for 'nazim': " + hashedPassword);
    }
}
