package com.medical.assessment.authms.api.controller;

import com.medical.assessment.authms.authentification.api.AuthentificationApi;
import com.medical.assessment.authms.authentification.model.AuthRequest;
import com.medical.assessment.authms.authentification.model.AuthResponse;
import com.medical.assessment.authms.domain.authentification.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthentificationApi {

    final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> login(@RequestBody final  AuthRequest authRequest) {
        log.info("Login request");
        final AuthResponse authResponse = authService.authenticateUser(authRequest);
        return ResponseEntity.ok(authResponse);
    }
}
