package com.example.estrera.controller;

import com.example.estrera.dto.*;
import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import com.example.estrera.service.JwtService;
import com.example.estrera.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserRepository userRepository, JwtService jwtService,
                          PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Email already exists"));
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Username already exists"));
        }


        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .is_active(true)
                .enabled(true)
                .created_at(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);


        String accessToken = jwtService.generateToken(savedUser.getUser_id(), savedUser.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(savedUser.getUser_id(), savedUser.getEmail());

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(UserResponse.builder()
                        .user_id(savedUser.getUser_id())
                        .username(savedUser.getUsername())
                        .email(savedUser.getEmail())
                        .isActive(savedUser.getIs_active())
                        .enabled(savedUser.isEnabled())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        String identifier = loginRequest.getUsernameOrEmail();
        String password = loginRequest.getPassword();

        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> {
                    if (identifier.contains("@")) {
                        return userRepository.findByEmail(identifier).orElse(null);
                    }
                    return null;
                });

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", "Invalid credentials"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", "Invalid credentials"));
        }

        if (!user.isEnabled()) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", "Account not verified"));
        }

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user.getUser_id(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getUser_id(), user.getEmail());

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(UserResponse.builder()
                        .user_id(user.getUser_id())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .isActive(user.getIs_active())
                        .enabled(user.isEnabled())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.status(403)
                .body(Collections.singletonMap("message", "Need authentication to access this endpoint"));
    }
}