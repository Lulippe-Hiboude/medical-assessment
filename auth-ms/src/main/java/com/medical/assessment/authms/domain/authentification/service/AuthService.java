package com.medical.assessment.authms.domain.authentification.service;

import com.medical.assessment.authms.authentification.model.AuthRequest;
import com.medical.assessment.authms.authentification.model.AuthResponse;

public interface AuthService {
    AuthResponse authenticateUser(final AuthRequest authRequest);
}
