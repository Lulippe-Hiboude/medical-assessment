package com.medical.assessment.authms.domain.authentification.service;

import com.medical.assessment.authms.authentification.model.AuthRequest;
import com.medical.assessment.authms.authentification.model.AuthResponse;
import com.medical.assessment.authms.domain.user_details.CustomUserDetailService;
import com.medical.assessment.authms.domain.authentification.mapper.AuthMapper;
import com.medical.assessment.authms.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomUserDetailService userDetailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user using the provided credentials and generates a JWT token
     * containing the user's roles.
     *
     * <p>The user is first loaded using its username. The provided password is then
     * compared with the encoded password stored for the user. If the credentials
     * are valid, a JWT token is generated and returned inside an {@link AuthResponse}.</p>
     *
     * @param authRequest the authentication request containing the username and password
     * @return an {@link AuthResponse} containing the generated JWT token
     * @throws UsernameNotFoundException if no user is found with the provided username
     * @throws BadCredentialsException if the provided password is invalid
     */
    @Override
    public AuthResponse authenticateUser(final AuthRequest authRequest) {
        final UserDetails user = userDetailService.loadUserByUsername(authRequest.getUsername());
        if(!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())){
            throw new BadCredentialsException("invalid password");
        }

        final List<String> roles = getUserRoles(user.getAuthorities());

        final String token = jwtService.generateToken(user.getUsername(),roles);

        return AuthMapper.INSTANCE.toAuthResponse(token);
    }

    private List<String> getUserRoles(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", StringUtils.EMPTY))
                .toList();
    }
}
