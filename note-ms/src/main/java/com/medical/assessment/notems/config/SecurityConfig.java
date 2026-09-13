package com.medical.assessment.notems.config;

import com.medical.assessment.notems.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    /**
     * Configures the application's Spring Security filter chain.
     *
     * <p>The security configuration:</p>
     * <ul>
     *     <li>disables CSRF protection because the API uses stateless JWT authentication;</li>
     *     <li>disables HTTP sessions by configuring {@link SessionCreationPolicy#STATELESS};</li>
     *     <li>allows unauthenticated access to Swagger and OpenAPI resources;</li>
     *     <li>requires the {@code DOCTOR} role to create notes;</li>
     *     <li>requires the {@code DOCTOR} role to retrieve patient notes;</li>
     *     <li>requires authentication for all other endpoints;</li>
     *     <li>returns HTTP {@code 401 UNAUTHORIZED} when authentication is required
     *         but not provided;</li>
     *     <li>returns HTTP {@code 403 FORBIDDEN} when the authenticated user does
     *         not have sufficient permissions;</li>
     *     <li>registers the JWT filter before Spring Security's
     *         {@link UsernamePasswordAuthenticationFilter}.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} instance used to configure web security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/favicon.ico",
                                "/webjars/**",
                                "/error/**")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET, "/notes/patient/**")
                        .hasRole("DOCTOR")

                        .requestMatchers(
                                HttpMethod.GET, "/notes/patient/*/note-content")
                        .hasRole("DOCTOR")

                        .requestMatchers(
                                HttpMethod.POST, "/notes")
                        .hasRole("DOCTOR")

                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
