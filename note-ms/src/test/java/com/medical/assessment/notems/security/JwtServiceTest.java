package com.medical.assessment.notems.security;

import com.medical.assessment.notems.security.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    public static final long EXPIRATION_DATE = 3600L;

    @InjectMocks
    private JwtService jwtService;

    private String secretKey;

    @BeforeEach
    void setup() {
        final SecretKey key = Jwts.SIG.HS256.key().build();
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        ReflectionTestUtils.setField(jwtService, "secretKey",
                secretKey);

        ReflectionTestUtils.setField(jwtService, "expirationDate", EXPIRATION_DATE);
    }

    @Test
    @DisplayName("should extract role correctly")
    void should_extract_role_correctly() {
        //given
        final String username = "user1";
        final String role = "DOCTOR";
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + EXPIRATION_DATE);
        final String token = generateToken(username, role,now, expiration);

        //when
        final Role extractedRole = jwtService.extractRole(token);

        //then
        assertEquals(Role.DOCTOR, extractedRole);
    }

    @Test
    @DisplayName("should extract username correctly")
    void should_extract_username_correctly() {
        //given
        final String username = "user1";
        final String role = "DOCTOR";
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + EXPIRATION_DATE);
        final String token = generateToken(username, role,now, expiration);

        //when
        final String extractedUsername = jwtService.extractUsername(token);

        //then
        assertEquals(username, extractedUsername);
    }

    private String generateToken(final String username, final String role, final Date now, final Date expiration) {

        return Jwts.builder()
                .subject(username)
                .claim("roles", List.of(role))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey(secretKey))
                .compact();
    }

    private SecretKey getSignKey(final String secretKey) {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return hmacShaKeyFor(keyBytes);
    }
}