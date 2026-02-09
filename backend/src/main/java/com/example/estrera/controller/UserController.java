package com.example.estrera.controller;

import com.example.estrera.dto.UserResponse;
import com.example.estrera.entity.User;
import com.example.estrera.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.status(403)
                .body(Collections.singletonMap("message", "Need authentication to access this endpoint"));
    }

}