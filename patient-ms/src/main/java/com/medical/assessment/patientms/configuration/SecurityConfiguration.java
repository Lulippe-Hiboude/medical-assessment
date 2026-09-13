package com.medical.assessment.patientms.configuration;

import com.medical.assessment.patientms.security.jwt.JwtFilter;
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

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;

    /**
     * Configures the application's Spring Security filter chain.
     *
     * <p>The security configuration:</p>
     * <ul>
     *     <li>disables CSRF protection because the API uses stateless
     *         JWT-based authentication;</li>
     *     <li>disables HTTP sessions by configuring
     *         {@link SessionCreationPolicy#STATELESS};</li>
     *     <li>allows unauthenticated access to Swagger and OpenAPI resources;</li>
     *     <li>requires the {@code DOCTOR} role to access patient risk profiles;</li>
     *     <li>requires the {@code DOCTOR} role to check whether a patient exists;</li>
     *     <li>allows users with the {@code ORGANIZER} or {@code DOCTOR} role
     *         to retrieve a patient by ID;</li>
     *     <li>requires the {@code ORGANIZER} role to retrieve all patients;</li>
     *     <li>requires the {@code ORGANIZER} role to create a patient;</li>
     *     <li>requires the {@code ORGANIZER} role to update a patient;</li>
     *     <li>requires authentication for all other endpoints;</li>
     *     <li>returns HTTP {@code 401 UNAUTHORIZED} when authentication is
     *         required but not provided;</li>
     *     <li>returns HTTP {@code 403 FORBIDDEN} when the authenticated user
     *         does not have the required role;</li>
     *     <li>registers the JWT filter before Spring Security's
     *         {@link UsernamePasswordAuthenticationFilter}.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} instance used to configure
     *             web security
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the security
     *         filter chain
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
                                HttpMethod.GET,"/patient/*/risk-profile")
                        .hasRole("DOCTOR")

                        .requestMatchers(
                                HttpMethod.GET,"/patient/*/exists")
                        .hasRole("DOCTOR")

                        .requestMatchers(
                                HttpMethod.GET, "/patient/*")
                        .hasAnyRole("ORGANIZER", "DOCTOR")

                        .requestMatchers(
                                HttpMethod.GET, "/patient")
                        .hasRole("ORGANIZER")

                        .requestMatchers(
                                HttpMethod.POST, "/patient/**")
                        .hasRole("ORGANIZER")

                        .requestMatchers(
                                HttpMethod.PATCH, "/patient/**")
                        .hasRole("ORGANIZER")

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
