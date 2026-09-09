package com.medical.assessment.authms.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;
    SecretKey key;

    @BeforeEach
    void setup() {
        key = Jwts.SIG.HS256.key().build();

        ReflectionTestUtils.setField(jwtService, "secretKey",
                Base64.getEncoder().encodeToString(key.getEncoded()));

        ReflectionTestUtils.setField(jwtService, "expirationDate", 3600000L);
    }

    @Test
    @DisplayName("should generate and parse token correctly")
    void should_generate_and_parse_token_correctly() {
        //given
        final String username = "user1";
        final String role = "DOCTOR";
        final List<String> roles = List.of(role);

        //when
        final String token = jwtService.generateToken(username, roles);

        //then
        final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        final String extractedUsername = claims.getSubject();
        final List<String> extractedRoles = claims.get("roles", List.class);

        assertThat(extractedUsername).isEqualTo(username);
        assertThat(extractedRoles).contains(role);
    }




}