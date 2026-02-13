package com.example.estrera.controller;

import com.example.estrera.dto.*;
import com.example.estrera.entity.User;
import com.example.estrera.service.JwtService;
import com.example.estrera.service.RefreshTokenService;
import com.example.estrera.service.UserService;
import com.example.estrera.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityUtil securityUtil;

    public AuthController(UserService userService, RefreshTokenService refreshTokenService, SecurityUtil securityUtil) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Map<String, Object> response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        // TODO: Implement refresh token logic
        return ResponseEntity.status(501).build();
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
//        try {
//            userService.logout(request.getRefreshToken());
//            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest()
//                    .body(Collections.singletonMap("message", e.getMessage()));
//        }
//    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout() {
//        try {
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == "anonymousUser") {
//                return ResponseEntity.status(403)
//                        .body(Collections.singletonMap("message", "Need authentication to logout"));
//            }
//
//            // Principal is your User object
//            User user = (User) auth.getPrincipal();
//
//            // Revoke refresh token(s) for this user;
//            refreshTokenService.revokeUserTokens(user.getUser_id());
//
//            // Clear security context
//            SecurityContextHolder.clearContext();
//
//            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
//        } catch (Exception e) {
//            return ResponseEntity.status(500)
//                    .body(Collections.singletonMap("message", "Logout failed"));
//        }
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            User user = securityUtil.getCurrentUser();
            refreshTokenService.revokeAllUserTokens(user.getUser_id());
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403)
                    .body(Collections.singletonMap("message", "Need authentication to logout"));
        }
    }




}