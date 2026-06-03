package com.labquest.backend.security;

import com.labquest.backend.config.LabQuestProperties;
import com.labquest.backend.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final LabQuestProperties properties;

    public JwtService(LabQuestProperties properties) {
        this.properties = properties;
    }

    public String generateToken(UserEntity user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + properties.jwt().expirationMs());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("tipo", user.getTipo().name())
                .claim("nome", user.getNome())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSigningKey() {
        byte[] keyBytes = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
