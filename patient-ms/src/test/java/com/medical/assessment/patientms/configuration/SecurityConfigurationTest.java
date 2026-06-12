package com.medical.assessment.patientms.configuration;

import com.medical.assessment.patientms.security.jwt.JwtService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigurationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("should_allow_public_endpoint")
    void should_allow_public_endpoint() throws Exception {
        //when & then
        mockMvc.perform(get("/auth/token")
                        .param("username", "test")
                        .param("role", "DOCTOR"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should_deny_access_to_protected_endpoint_without_token")
    void should_deny_access_to_protected_endpoint_without_token() throws Exception {
        //when & then
        mockMvc.perform(get("/patient/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should_allow_access_to_protected_endpoint_with_valid_token")
    void should_allow_access_to_protected_endpoint_with_valid_token() throws Exception {
        // given
        final String token = jwtService.generateToken("test", "DOCTOR");


        // when & then
        mockMvc.perform(get("/patient/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should_fail_when_token_is_expired")
    void should_fail_when_token_is_expired() throws Exception {
        // given
        final SecretKey key = Jwts.SIG.HS256.key().build();

        final Instant now = Instant.now();

        final String expiredToken = Jwts.builder()
                .subject("test")
                .claim("role", "DOCTOR")
                .issuedAt(Date.from(now.minusSeconds(3600)))
                .expiration(Date.from(now.minusSeconds(1800)))
                .signWith(key)
                .compact();

        // when& then
        mockMvc.perform(get("/patient/{id}", 1L)
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should deny access to unauthorized role")
    void should_deny_access_to_unauthorized_role() throws Exception {
        // given
        final String token = jwtService.generateToken("test", "DOCTOR");

        // when & then
        mockMvc.perform(get("/patient")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
