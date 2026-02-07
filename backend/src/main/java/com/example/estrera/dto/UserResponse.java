package com.example.estrera.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long user_id;
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean enabled;
}