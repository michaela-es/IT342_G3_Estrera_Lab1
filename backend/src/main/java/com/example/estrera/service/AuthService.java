package com.example.estrera.service;

import com.example.estrera.dto.LoginRequest;
import com.example.estrera.dto.RegisterRequest;
import com.example.estrera.dto.AuthResponse;
import com.example.estrera.dto.UserResponse;
import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setIs_active(true);

        user = userRepository.save(user);

        return Map.of(
                "message", "Registration successful! Please verify your email.",
                "userId", user.getUser_id(),
                "email", user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = findUserByUsernameOrEmail(request.getUsernameOrEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(mapToUserResponse(user))
                .build();
    }

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToUserResponse(user);
    }

    public String extractTokenFromCookies(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(cookieName + " not found"));
        }
        throw new IllegalArgumentException("No cookies found");
    }

    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> {
                    if (usernameOrEmail.contains("@")) {
                        return userRepository.findByEmail(usernameOrEmail)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
                    }
                    throw new IllegalArgumentException("Invalid credentials");
                });
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
