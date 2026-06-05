package com.medical.assessment.patientms.security.jwt;

import com.medical.assessment.patientms.security.jwt.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationDate;

    public Role extractRole(final String token) {
        final String role = extractClaims(token).get("role", String.class);
        return Role.fromValue(role);
    }

    public String extractUsername(final String token) {
        return extractClaims(token).getSubject();
    }

    public String generateToken(final String username, final String role) {
        final Role validRole = Role.fromValue(role);

        if(StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expirationDate);
        return Jwts.builder()
                .subject(username)
                .claim("role", validRole.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey(secretKey))
                .compact();
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