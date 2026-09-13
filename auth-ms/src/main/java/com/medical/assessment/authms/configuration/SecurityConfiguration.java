package com.medical.assessment.authms.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    /**
     * Creates the password encoder used to securely hash and verify user passwords.
     *
     * <p>Passwords are encoded using the BCrypt hashing algorithm.</p>
     *
     * @return a {@link PasswordEncoder} configured with BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the application's Spring Security filter chain.
     *
     * <p>The application is configured to:</p>
     * <ul>
     *     <li>Disable CSRF protection, as the application uses stateless authentication.</li>
     *     <li>Disable HTTP sessions by using {@link SessionCreationPolicy#STATELESS}.</li>
     *     <li>Allow unauthenticated access to authentication endpoints and OpenAPI/Swagger resources.</li>
     *     <li>Require authentication for all other endpoints.</li>
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
                                "/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/favicon.ico",
                                "/webjars/**",
                                "/error/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .build();
    }
}
