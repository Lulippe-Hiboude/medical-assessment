package com.medical.assessment.authms.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;

    /**
     * Generates a signed JWT token for the specified user.
     *
     * <p>The generated token contains the username as its subject and the user's
     * roles as a custom claim. The token also includes its issue date and expiration
     * date and is signed using the configured secret key with the HS256 algorithm.</p>
     *
     * @param username the username to include as the JWT subject
     * @param roles the roles to include in the JWT {@code roles} claim
     * @return a signed and compact JWT token
     */
    public String generateToken(final String username, final List<String> roles) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey(secretKey),Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSignKey(final String secretKey) {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return hmacShaKeyFor(keyBytes);
    }
}