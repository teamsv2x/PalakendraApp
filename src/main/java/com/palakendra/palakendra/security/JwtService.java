package com.palakendra.palakendra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes
    ) {
        // requires a 256-bit+ secret (>= 32 bytes). keep your 64+ char secret in application.yml
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationMinutes * 60;
    }

    /** Create a signed JWT with subject=username and optional claims. */
    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Parse & return claims (verifies signature and expiration). */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Convenience: subject (username) from token. */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** Validate token against current user (signature + expiry + subject match). */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims c = getClaims(token); // throws if invalid/expired
            String username = c.getSubject();
            return username != null && username.equals(userDetails.getUsername());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
