package com.medical.assessment.patientms.security.jwt;

import com.medical.assessment.patientms.security.jwt.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationDate))
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
