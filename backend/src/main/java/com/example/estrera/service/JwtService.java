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

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(Long userId) {
        System.out.println("DEBUG: Generating access token for userId=" + userId);

        try {
            return Jwts.builder()
                    .claim("userId", userId)
                    .claim("type", "access")
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("ERROR generating access token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    public String generateRefreshToken(Long userId) {
        System.out.println("DEBUG: Generating refresh token for userId=" + userId);

        try {
            return Jwts.builder()
                    .claim("userId", userId)
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

    public String generateVerificationToken(String email) {
        System.out.println("DEBUG: Generating verification token for email=" + email);

        try {
            return Jwts.builder()
                    .claim("email", email)
                    .claim("type", "verification")
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 900000))
                    .signWith(getSignInKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception e) {
            System.err.println("ERROR generating verification token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate verification token", e);
        }
    }


    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenType(String token, String expectedType) {
        try {
            String type = extractTokenType(token);
            return expectedType.equals(type);
        } catch (Exception e) {
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
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            System.err.println("ERROR in getSignInKey: " + e.getMessage());
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