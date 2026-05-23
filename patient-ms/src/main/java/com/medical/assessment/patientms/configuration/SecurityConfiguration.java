package com.medical.assessment.patientms.configuration;

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

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/css/**", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET,"/patient/**").hasAnyRole("ORGANIZER", "DOCTOR")
                        .requestMatchers( HttpMethod.POST, "/patient/**").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.PUT,"/patient/**").hasRole("ORGANIZER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore()
                .build()

