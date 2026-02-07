package com.example.estrera.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private LocalDateTime last_login;

    @Column(nullable = false)
    private Boolean is_active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }
}
