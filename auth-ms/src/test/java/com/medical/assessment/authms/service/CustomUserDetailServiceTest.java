package com.medical.assessment.authms.service;

import com.medical.assessment.authms.repository.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {
    @Mock
    private InMemoryUserRepository inMemoryUserRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    @DisplayName("should load user by username organizer")
    void should_load_user_by_username_organizer() {
        //given
        final String username = "organizer";
        final String roleOrganizer = "ROLE_ORGANIZER";
        final UserDetails userDetails = User.builder()
                .username(username)
                .password("encodedPassword")
                .authorities(roleOrganizer)
                .build();
        given(inMemoryUserRepository.findByUsername(username)).willReturn(userDetails);

        //when
        final UserDetails loadedUser = customUserDetailService.loadUserByUsername(username);

        //then
        assertThat(loadedUser).isNotNull();
        assertThat(loadedUser.getUsername()).isEqualTo(username);
        assertThat(loadedUser.getAuthorities()).hasSize(1);
        assertThat(loadedUser.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(roleOrganizer);
        assertThat(loadedUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("should load user by username doctor")
    void should_load_user_by_username_doctor() {
        //given
        final String username = "doctor";
        final String roleDoctor = "ROLE_DOCTOR";
        final UserDetails userDetails = User.builder()
                .username(username)
                .password("encodedPassword")
                .authorities(roleDoctor)
                .build();
        given(inMemoryUserRepository.findByUsername(username)).willReturn(userDetails);

        //when
        final UserDetails loadedUser = customUserDetailService.loadUserByUsername(username);

        //then
        assertThat(loadedUser).isNotNull();
        assertThat(loadedUser.getUsername()).isEqualTo(username);
        assertThat(loadedUser.getAuthorities()).hasSize(1);
        assertThat(loadedUser.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly(roleDoctor);
        assertThat(loadedUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("should throw UsernameNotFoundException for unknown username")
    void should_throw_UsernameNotFoundException_for_unknown_username() {
        //given
        final String username = "doctor";
        final String roleDoctor = "ROLE_DOCTOR";
        final UserDetails userDetails = User.builder()
                .username(username)
                .password("encodedPassword")
                .authorities(roleDoctor)
                .build();
        given(inMemoryUserRepository.findByUsername(username)).willReturn(null);

        //when
            assertThrows(UsernameNotFoundException.class, () -> customUserDetailService.loadUserByUsername(username));
    }

}