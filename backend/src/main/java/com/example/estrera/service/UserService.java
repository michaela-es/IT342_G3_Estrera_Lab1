package com.example.estrera.service;

import com.example.estrera.dto.*;
import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .is_active(true)
                .enabled(true) // Set to true if no email verification
                .created_at(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser.getUser_id(), savedUser.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(savedUser.getUser_id(), savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24 hours
                .user(mapToUserResponse(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by username or email
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

        // Update last login
        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user.getUser_id(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(mapToUserResponse(user))
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
                .lastLogin(user.getLast_login())
                .createdAt(user.getCreated_at())
                .build();
    }
}