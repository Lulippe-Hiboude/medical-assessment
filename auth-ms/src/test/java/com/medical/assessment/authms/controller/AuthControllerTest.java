package com.medical.assessment.authms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.assessment.authms.api.controller.AuthController;
import com.medical.assessment.authms.authentification.model.AuthRequest;
import com.medical.assessment.authms.authentification.model.AuthResponse;
import com.medical.assessment.authms.configuration.SecurityConfiguration;
import com.medical.assessment.authms.domain.authentification.service.AuthService;
import com.medical.assessment.authms.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("should return 200 OK when login request is valid")
    void should_return_200_OK_when_login_request_is_valid() throws Exception {
        // given
        final String doctor = "doctor";
        final String password = "password";
        final String mockedToken = "mockedToken";

        final AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(doctor);
        authRequest.setPassword(password);

        final AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(mockedToken);

        given(authService.authenticateUser(authRequest)).willReturn(authResponse);

        // when & then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockedToken));

    }

    @Test
    @DisplayName("should return 400 when credentials are invalid")
    void should_return_400_when_credentials_are_invalid() throws Exception {
        //given
        final String doctor = "doctor";
        final String invalidPassword = "invalidPassword";
        final AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(doctor);
        authRequest.setPassword(invalidPassword);

        given(authService.authenticateUser(authRequest)).willThrow(new BadCredentialsException("Invalid credentials"));

        //when & then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @DisplayName("should return 404 when username not found")
    void should_return_404_when_username_not_found() throws Exception {
        // given
        final String unknown = "unknown";
        final String password = "password";
        final String mockedToken = "mockedToken";

        final AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(unknown);
        authRequest.setPassword(password);

        given(authService.authenticateUser(authRequest)).willThrow(new UsernameNotFoundException("user not found"));

        // when & then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("user not found"));
    }
}