package com.example.estrera.service;

import com.example.estrera.entity.RefreshToken;
import com.example.estrera.entity.User;
import com.example.estrera.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpirationMs;

    @Transactional
    public String createRefreshToken(Long userId, String email) {
        User user = User.builder().user_id(userId).build();

        revokeAllUserTokens(userId);

        String token = jwtService.generateRefreshToken(userId, email);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(token))  // Hash the token before storing
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs))
                .revokedAt(null)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public RefreshToken verifyRefreshToken(String token) {
        String tokenHash = hashToken(token);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (refreshToken.getRevokedAt() != null) {
            throw new SecurityException("Refresh token was revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
            throw new SecurityException("Refresh token expired");
        }

        if (!jwtService.isTokenValid(token)) {
            throw new SecurityException("Invalid refresh token");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        String tokenHash = hashToken(token);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(refreshToken -> {
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.findByUserId(userId).forEach(refreshToken -> {
            if (refreshToken.getRevokedAt() == null) {
                refreshToken.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(refreshToken);
            }
        });
    }

    private String hashToken(String token) {
        // Simple hash - use BCrypt or SHA-256 in production
        return Integer.toHexString(token.hashCode());

        // Better: Use BCrypt
        // return BCrypt.hashpw(token, BCrypt.gensalt());
    }
}