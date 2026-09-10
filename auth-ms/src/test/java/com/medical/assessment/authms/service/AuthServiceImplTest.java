package com.medical.assessment.authms.service;

import com.medical.assessment.authms.authentification.model.AuthRequest;
import com.medical.assessment.authms.authentification.model.AuthResponse;
import com.medical.assessment.authms.domain.authentification.service.AuthServiceImpl;
import com.medical.assessment.authms.domain.user_details.CustomUserDetailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private CustomUserDetailService userDetailService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Test
    @DisplayName("should return token when credentials are valid")
    void should_return_token_when_credentials_are_valid() {
        //given
        final String doctor = "doctor";
        final  String password = "password";
        final String mockedToken = "mockedToken";
        final AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(doctor);
        authRequest.setPassword(password);

        final UserDetails userDetails = User.builder()
                .username(doctor)
                .password("encodedPassword")
                .authorities("ROLE_DOCTOR")
                .build();

        given(userDetailService.loadUserByUsername(doctor)).willReturn(userDetails);
        given(passwordEncoder.matches(password, userDetails.getPassword())).willReturn(true);
        given(jwtService.generateToken(doctor, List.of("DOCTOR"))).willReturn(mockedToken);

        //when
        final AuthResponse authResponse = authServiceImpl.authenticateUser(authRequest);

        //then
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isEqualTo(mockedToken);
        verify(jwtService).generateToken(doctor, List.of("DOCTOR"));
    }

    @Test
    @DisplayName("should throw BadCredentialsException when password is invalid")
    void should_throw_BadCredentialsException_when_password_is_invalid() {
        //given
        final String doctor = "doctor";
        final  String invalidPassword = "invalidPassword";
        final AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(doctor);
        authRequest.setPassword(invalidPassword);

        final UserDetails userDetails = User.builder()
                .username(doctor)
                .password("encodedPassword")
                .authorities("ROLE_DOCTOR")
                .build();

        given(userDetailService.loadUserByUsername(doctor)).willReturn(userDetails);
        given(passwordEncoder.matches(invalidPassword, userDetails.getPassword())).willReturn(false);

        //when & then
        assertThrows(BadCredentialsException.class, () -> authServiceImpl.authenticateUser(authRequest));
        verifyNoInteractions(jwtService);
    }

}