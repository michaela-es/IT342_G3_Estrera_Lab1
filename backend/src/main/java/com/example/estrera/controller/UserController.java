package com.example.estrera.controller;

import com.example.estrera.dto.ProfileResponse;
import com.example.estrera.dto.UserResponse;
import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import com.example.estrera.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final  SecurityUtil securityUtil;

    public UserController(UserRepository userRepository, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = securityUtil.getCurrentUser();
            ProfileResponse response = new ProfileResponse(
                    user.getUsername(),
                    user.getEnabled(),
                    user.getEmail()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403)
                    .body(Collections.singletonMap("message", "Need authentication to access this endpoint"));
        }
    }
}