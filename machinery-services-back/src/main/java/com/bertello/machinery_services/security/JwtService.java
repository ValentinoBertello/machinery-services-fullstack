package com.bertello.machinery_services.security;

import com.bertello.machinery_services.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24 * 15;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    // Transformamos la secret key de string a "cryptographic format"
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserEntity userEntity) {
        return Jwts
                .builder()
                .subject(userEntity.getId().toString())
                .claim("email", userEntity.getEmail())
                .claim("roles", Set.of(userEntity.getRole()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSecretKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }
}