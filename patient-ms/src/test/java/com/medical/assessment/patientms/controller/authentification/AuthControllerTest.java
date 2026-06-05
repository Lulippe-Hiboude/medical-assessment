package com.medical.assessment.patientms.controller.authentification;

import com.medical.assessment.patientms.security.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("should return AuthTokenResponse when username and a valid role are provided")
    void shouldReturnAuthTokenResponseWhenUsernameAndValidRoleProvided() throws Exception {
        // Given
        final String username = "testuser";
        final String role = "ORGANIZER";
        final String token = "mocked-jwt-token";

        given(jwtService.generateToken(anyString(), anyString())).willReturn(token);

        // When & Then
        mockMvc.perform(get("/auth/token")
                        .param("username", username)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    @DisplayName("should return Bad Request when an invalid role is provided")
    void shouldReturnBadRequestWhenInvalidRoleProvided() throws Exception {
        // Given
        final String username = "testuser";
        final String invalidRole = "INVALID_ROLE";

        given(jwtService.generateToken(anyString(), anyString())).willThrow(new IllegalArgumentException());

        // When & Then
        mockMvc.perform(get("/auth/token")
                        .param("username", username)
                        .param("role", invalidRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return Bad Request when username is missing")
    void shouldReturnBadRequestWhenUsernameIsMissing() throws Exception {
        // Given
        final String role = "ORGANIZER";

        given(jwtService.generateToken(anyString(), anyString())).willThrow(new IllegalArgumentException());
        // When & Then
        mockMvc.perform(get("/auth/token")
                        .param("username", "")
                        .param("role", role))
                .andExpect(status().isBadRequest());
    }
}