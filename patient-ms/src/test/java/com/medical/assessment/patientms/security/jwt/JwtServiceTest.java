package com.medical.assessment.patientms.security.jwt;

import com.medical.assessment.patientms.security.jwt.enums.Role;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        final SecretKey key = Jwts.SIG.HS256.key().build();

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

        //when
        final String token = jwtService.generateToken(username, role);

        //then
        assertEquals(username, jwtService.extractUsername(token));
        assertEquals(Role.DOCTOR, jwtService.extractRole(token));
    }

    @Test
    @DisplayName("should extract role correctly")
    void should_extract_role_correctly() {
        //given
        final String username = "user1";
        final String role = "DOCTOR";
        final String token = jwtService.generateToken(username, role);

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
        final String token = jwtService.generateToken(username, role);

        //when
        final String extractedUsername = jwtService.extractUsername(token);

        //then
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException if role is invalid")
    void should_throw_IllegalArgumentException_if_role_is_invalid() {
        //given
        final String username = "user1";
        final String role = "INVALID_ROLE";

        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(username,role));

        //then
        assertEquals("Invalid role value: " + role, exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException if role is missing")
    void should_throw_IllegalArgumentException_if_role_is_missing() {
        //given
        final String username = "user1";
        final String role = " ";

        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(username,role));

        //then
        assertEquals("Role is missing in the token", exception.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException if username is missing")
    void should_throw_IllegalArgumentException_if_username_is_missing() {
        //given
        final String username = " ";
        final String role = "DOCTOR";

        //when
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(username,role));

        //then
        assertEquals("Username cannot be blank", exception.getMessage());
    }
}