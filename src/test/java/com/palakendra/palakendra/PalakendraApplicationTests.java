package com.palakendra.palakendra;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(PalakendraApplicationTests.TestBeans.class)
class PalakendraApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestBeans {
		@Bean
		AuthenticationManager authenticationManager() {
			return auth -> new UsernamePasswordAuthenticationToken(
					auth.getPrincipal(), auth.getCredentials(), java.util.Collections.emptyList());
		}
	}
}
