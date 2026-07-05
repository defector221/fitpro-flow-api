package com.fitpro.security;

import com.fitpro.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(UserPrincipal principal) {
        return buildToken(principal, jwtProperties.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(UserPrincipal principal) {
        return buildToken(principal, jwtProperties.getRefreshTokenExpirationMs());
    }

    private String buildToken(UserPrincipal principal, long expirationMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getId().toString())
                .claim("email", principal.getEmail())
                .claim("gymId", principal.getGymId() != null ? principal.getGymId().toString() : null)
                .claim("branchId", principal.getBranchId() != null ? principal.getBranchId().toString() : null)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(getSigningKey())
                .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public boolean isTokenValid(String token, UserPrincipal principal) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject().equals(principal.getId().toString())
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationMs() {
        return jwtProperties.getAccessTokenExpirationMs();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
