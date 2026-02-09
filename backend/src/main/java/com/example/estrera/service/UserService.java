package com.example.estrera.service;

import com.example.estrera.dto.*;
import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIs_active(true);
        user.setEnabled(false);
        user.setCreated_at(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateToken(savedUser.getUser_id(), savedUser.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(savedUser.getUser_id(), savedUser.getEmail());

        UserResponse userResponse = mapToUserResponse(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userResponse)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> {
                    if (request.getUsernameOrEmail().contains("@")) {
                        return userRepository.findByEmail(request.getUsernameOrEmail()).orElse(null);
                    }
                    return null;
                });

        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Account not verified");
        }

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getUser_id(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id(), user.getEmail());

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(userResponse)
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .user_id(user.getUser_id())
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(user.getIs_active())
                .enabled(user.isEnabled())
                .build();
    }
}