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

    /**
     * Loads a user by its username.
     *
     * <p>This method retrieves the user from the repository and returns it as a
     * {@link UserDetails} instance, which can then be used by Spring Security
     * during the authentication process.</p>
     *
     * @param username the username of the user to load
     * @return the {@link UserDetails} associated with the given username
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("user " + username +  " not found."));
    }
}
