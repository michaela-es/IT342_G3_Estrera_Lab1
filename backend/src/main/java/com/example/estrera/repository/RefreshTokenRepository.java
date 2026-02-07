package com.example.estrera.repository;

import com.example.estrera.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);


    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.user_id = :userId")
    List<RefreshToken> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.user_id = :userId AND rt.revokedAt IS NOT NULL")
    void deleteRevokedTokensByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revokedAt = CURRENT_TIMESTAMP WHERE rt.user.user_id = :userId AND rt.revokedAt IS NULL")
    void revokeAllUserTokens(@Param("userId") Long userId);
}