package com.example.estrera.service;

import com.example.estrera.entity.RefreshToken;
import com.example.estrera.entity.User;
import com.example.estrera.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public String createRefreshToken(Long userId, String email) {
        User user = new User();
        user.setUser_id(userId);

        revokeAllUserTokens(userId);

        String token = jwtService.generateRefreshToken(userId, email);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(token));
        refreshToken.setIssuedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs));
        refreshToken.setRevokedAt(null);

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
        return Integer.toHexString(token.hashCode());
    }
}