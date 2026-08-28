package com.medical.assessment.notems.config;

import com.medical.assessment.notems.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;

    @Test
    @DisplayName("should_deny_access_to_protected_endpoint_without_token")
    void should_deny_access_to_protected_endpoint_without_token() throws Exception {
        //when & then
        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should_allow_access_to_protected_endpoint_with_valid_token")
    void should_allow_access_to_protected_endpoint_with_valid_token() throws Exception {
        // given

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("test", "DOCTOR", now, expiration);

        // when & then
        mockMvc.perform(get("/notes/patient/1", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should_fail_when_token_is_expired")
    void should_fail_when_token_is_expired() throws Exception {
        // given
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() - expirationDate);
        final String expiredToken = generateToken("test", "DOCTOR", now, expiration);

        // when& then
        mockMvc.perform(get("/notes/patient/1", 1L)
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should deny access to unauthorized role")
    void should_deny_access_to_unauthorized_role() throws Exception {
        // given

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        final String token = generateToken("test", "ORGANIZER", now, expiration);

        // when & then
        mockMvc.perform(get("/notes/patient/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String generateToken(final String username, final String role, final Date now, final Date expiration ) {

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