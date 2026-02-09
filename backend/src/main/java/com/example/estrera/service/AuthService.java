package com.example.estrera.service;

import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService (UserRepository userRepository,PasswordEncoder passwordEncoder ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticateUser(String usernameOrEmail, String password) {
        if (!StringUtils.hasText(usernameOrEmail) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Username/email and password are required");
        }

        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> {
                    if (usernameOrEmail.contains("@")) {
                        return userRepository.findByEmail(usernameOrEmail).orElse(null);
                    }
                    return null;
                });

        if (user == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Account is not verified");
        }

        return user;
    }
}