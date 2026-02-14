package com.example.estrera.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Long userId, String email) {
        System.out.println("DEBUG: Generating token for userId=" + userId + ", email=" + email);

        try {
            return Jwts.builder()
                    .claim("userId", userId)
                    .claim("email", email)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("ERROR generating token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String generateRefreshToken(Long userId, String email) {
        System.out.println("DEBUG: Generating refresh token for userId=" + userId);

        try {
            return Jwts.builder()
                    .claim("userId", userId)
                    .claim("email", email)
                    .claim("type", "refresh")
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("ERROR generating refresh token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("Error parsing token: " + e.getMessage());
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        try {
            System.out.println("DEBUG: Secret key: " + (secretKey != null ? secretKey.substring(0, Math.min(20, secretKey.length())) + "..." : "null"));
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            System.out.println("DEBUG: Key bytes length: " + keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            System.err.println("ERROR in getSignInKey: " + e.getMessage());
            System.err.println("Secret key: " + secretKey);
            throw new RuntimeException("Invalid JWT secret key", e);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}