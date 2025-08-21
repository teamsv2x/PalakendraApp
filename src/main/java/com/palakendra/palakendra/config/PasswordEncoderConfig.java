package com.palakendra.palakendra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test") // only active during tests
public class PasswordEncoderConfig {


    @Bean(name = "noopasswordEncoder")
    public PasswordEncoder noopasswordEncoder() {
        // strength 10 is a sensible default; lower (e.g. 4) if you want faster tests
        return new BCryptPasswordEncoder(10);
    }
}