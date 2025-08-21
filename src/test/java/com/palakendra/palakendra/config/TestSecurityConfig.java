// src/test/java/com/palakendra/palakendra/config/TestSecurityConfig.java
package com.palakendra.palakendra.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    SecurityFilterChain testSecurity(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { // keep it here ONLY in tests
        return new BCryptPasswordEncoder();
    }
}
