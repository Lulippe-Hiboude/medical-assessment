package com.medical.assessment.notems.security;

import com.medical.assessment.notems.security.enums.Role;
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

    /**
     * Processes the JWT bearer token contained in the HTTP {@code Authorization}
     * header.
     *
     * <p>If the request does not contain a bearer token, the request is passed
     * through the filter chain without authentication. When a token is present,
     * it is extracted and used to authenticate the current request.</p>
     *
     * <p>If an error occurs while processing the JWT, the security context is
     * cleared and the request continues through the filter chain. Spring Security
     * is then responsible for handling the request as unauthenticated or
     * unauthorized according to the configured security rules.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain used to continue request processing
     * @throws ServletException if an error occurs during servlet processing
     * @throws IOException if an I/O error occurs while processing the request
     */
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
        log.debug("Extracted role from token: {}", role);
        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        log.debug("Created authority from role: {}", authority.getAuthority());
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtService.extractUsername(token),
                null,
                singletonList(authority)
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authToken);
    }
}
