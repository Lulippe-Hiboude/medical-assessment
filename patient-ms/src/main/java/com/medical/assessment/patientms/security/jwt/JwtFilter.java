package com.medical.assessment.patientms.security.jwt;

import com.medical.assessment.patientms.security.jwt.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.util.Collections.singletonList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;

        }
        final String token = authHeader.substring(7);

        try {
            authenticateFromToken(token);
            filterChain.doFilter(request, response);
        } catch (final Exception e) {
            log.error("JWT error", e);
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    private void authenticateFromToken(String token) {
        final Role role = jwtService.extractRole(token);
        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtService.extractUsername(token),
                null,
                singletonList(authority)
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authToken);
    }
}
