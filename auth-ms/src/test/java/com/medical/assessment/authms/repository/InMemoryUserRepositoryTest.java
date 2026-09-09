package com.medical.assessment.authms.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InMemoryUserRepositoryTest {
    @InjectMocks
    private InMemoryUserRepository inMemoryUserRepository;

    @Test
    @DisplayName("should find by username for organizer")
    void should_find_by_username_for_organizer() {

        // When
        final String organizer = "organizer";
        final UserDetails userDetails = inMemoryUserRepository.findByUsername(organizer);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(organizer);
    }

    @Test
    @DisplayName("should find by username for doctor")
    void should_find_by_username_for_doctor() {

        // When
        final String doctor = "doctor";
        final UserDetails userDetails = inMemoryUserRepository.findByUsername(doctor);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(doctor);
    }

    @Test
    @DisplayName("should return null for unknown username")
    void should_return_null_for_unknown_username() {
        // When
        final String unknown = "unknown";
        final UserDetails userDetails = inMemoryUserRepository.findByUsername(unknown);

        // Then
        assertThat(userDetails).isNull();
    }
}