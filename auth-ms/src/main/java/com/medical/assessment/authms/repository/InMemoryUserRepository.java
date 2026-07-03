package com.medical.assessment.authms.repository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, UserDetails> inMemoryUsers;

    public InMemoryUserRepository() {

        this.inMemoryUsers = Map.of(
                "organizer", User.withUsername("organizer")
                        .password("$2a$10$DVtL7uZTb.fh1veYXAnYgu/sApn1e16K4cjxBOLnSYEzE1umaqtu2")
                        .roles("ORGANIZER")
                        .build(),
                "doctor",
                User.withUsername("doctor")
                        .password("$2a$10$DVtL7uZTb.fh1veYXAnYgu/sApn1e16K4cjxBOLnSYEzE1umaqtu2")
                        .roles("DOCTOR")
                        .build()
        );
    }

    @Override
    public UserDetails findByUsername(final String username) {
        return inMemoryUsers.get(username);
    }
}
