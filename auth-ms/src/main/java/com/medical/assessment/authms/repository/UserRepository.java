package com.medical.assessment.authms.repository;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository {
    UserDetails findByUsername(final String username);
}
