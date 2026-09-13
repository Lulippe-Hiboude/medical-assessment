package com.medical.assessment.notems.security;

import com.medical.assessment.notems.security.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;

    /**
     * Extracts the user's role from a JWT token.
     *
     * <p>The role is retrieved from the {@code roles} claim of the token
     * and converted to the corresponding {@link Role} value.</p>
     *
     * @param token the JWT token containing the user's roles
     * @return the user's {@link Role}
     */
    public Role extractRole(final String token) {
        final List<String> roles = extractClaims(token).get("roles", List.class);
        log.debug("Extracted roles from token: {}", roles);
        return Role.fromValue(roles.getFirst());
    }

    /**
     * Extracts the username from a JWT token.
     *
     * <p>The username is retrieved from the subject claim of the token.</p>
     *
     * @param token the JWT token containing the username
     * @return the username stored in the token
     */
    public String extractUsername(final String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSignKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey(final String secretKey) {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return hmacShaKeyFor(keyBytes);
    }
}
