package com.example.estrera.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
//    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}