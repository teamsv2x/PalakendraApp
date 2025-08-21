package com.palakendra.palakendra.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    JwtService jwt = new JwtService("this-is-a-long-64plus-char-secret-for-tests-1234567890-abcdef-xyz", 60);

    @Test
    void generate_and_parse_roundtrip() {
        String token = jwt.generate("admin", Map.of("role", "ADMIN", "uid", 1));
        String sub = jwt.getUsername(token);
        var claims = jwt.getClaims(token);
        assertThat(sub).isEqualTo("admin");
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_comparesSubject() {
        String token = jwt.generate("alice", Map.of());
        var ud = User.withUsername("alice").password("x").roles("ADMIN").build();
        assertThat(jwt.isTokenValid(token, ud)).isTrue();
    }
}
