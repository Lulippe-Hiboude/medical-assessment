package com.medical.assessment.patientms.security.jwt;

import com.medical.assessment.patientms.security.jwt.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("should_skip_filter_when_no_auth_header")
    void should_skip_filter_when_no_auth_header() throws Exception {
        //given
        given(request.getHeader("Authorization")).willReturn(null);

        //when
        jwtFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("should_skip_filter_when_header_is_not_bearer")
    void should_skip_filter_when_header_is_invalid() throws Exception {
        //given
        given(request.getHeader("Authorization")).willReturn("Basic abc");

        //when
        jwtFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("should_authenticate_user_when_token_is_valid")
    void should_authenticate_user_when_token_is_valid() throws Exception {
        //given
        given(request.getHeader("Authorization")).willReturn("Bearer token");
        given(jwtService.extractRole("token")).willReturn(Role.DOCTOR);
        given(jwtService.extractUsername("token")).willReturn("user1");

        //when
        jwtFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(jwtService).extractRole("token");
        verify(jwtService).extractUsername("token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should_clear_context_when_token_invalid")
    void should_clear_context_when_token_invalid() throws Exception {
        //given
        given(request.getHeader("Authorization")).willReturn("Bearer token");
        given(jwtService.extractRole("token"))
                .willThrow(new IllegalArgumentException("Invalid role value"));

        //when
        jwtFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
    }
}