package com.medical.assessment.authms.domain.user_details;

import com.medical.assessment.authms.repository.InMemoryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final InMemoryUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("user " + username +  " not found."));
    }
}
