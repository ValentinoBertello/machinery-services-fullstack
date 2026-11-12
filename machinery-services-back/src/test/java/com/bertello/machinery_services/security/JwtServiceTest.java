package com.bertello.machinery_services.security;

import com.bertello.machinery_services.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos el secretKey manualmente sin levantar Spring
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey",
                "testSecretKeySuperLargaParaTests1234567890abcdefgHIJK");
    }

    @Test
    void testJwtGenerationAndExtraction() {
        // Arrange: Create a test user
        UserEntity user = UserEntity.builder()
                .email("valen@gmail.com")
                .password("1234")
                .role("ADMIN")
                .id(4L)
                .build();

        // Act: Generate token
        String token = jwtService.generateAccessToken(user);
        log.info("Generated Token: " + token);

        // Assert: Token should not be null or empty
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Act: Extract user ID from token
        Long extractedId = jwtService.getUserIdFromToken(token);
        log.info("Extracted User ID: " + extractedId);

        // Assert: Extracted ID should match original
        assertEquals(user.getId(), extractedId);
    }

    @Test
    void testInvalidTokenSignature() {
        // Create a token
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .role("ADMIN")
                .build();
        String token = jwtService.generateAccessToken(user);

        // Tamper with the token (change one character)
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        // This should throw SignatureException
        assertThrows(SignatureException.class, () -> {
            jwtService.getUserIdFromToken(tamperedToken);
        });
    }
}