package com.medical.assessment.authms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.authms.authentification.model.AuthRequest;
import com.medical.assessment.authms.authentification.model.AuthResponse;
import com.medical.assessment.authms.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;

import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;


    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;

    @Test
    @DisplayName("should return valid token when login request is valid")
    void should_return_valid_token_when_login_request_is_valid() throws Exception {
        // given
        final String doctor = "doctor";
        final String password = "password";

        final AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(doctor);
        authRequest.setPassword(password);

        // when

        final MvcResult result = mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        final AuthResponse response = objectMapper.readValue(result.getResponse()
                .getContentAsString(), AuthResponse.class);

        final SecretKey key = hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(response.getToken())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(doctor);
        assertThat((List<String>) claims.get("roles", List.class)).contains("DOCTOR");
    }

    @Test
    @DisplayName("should return 400 when credentials are invalid")
    void should_return_400_when_credentials_are_invalid() throws Exception {
        // given
        final AuthRequest authRequest = new AuthRequest("doctor", "wrong-password");

        // when / then

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 404 when username not found")
    void should_return_404_when_username_not_found() throws Exception {
        // given
        final AuthRequest authRequest = new AuthRequest("unknown", "password");

        // when / then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isNotFound());
    }

}
