// src/main/java/com/palakendra/palakendra/PalakendraApplication.java
package com.palakendra.palakendra;

import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PalakendraApplication {
	public static void main(String[] args) {
		SpringApplication.run(PalakendraApplication.class, args);
	}

	// Do NOT run during tests, and only if a UserRepository bean actually exists
	@Bean
	@Profile("!test")
	@ConditionalOnBean(UserRepository.class)
	CommandLineRunner init(UserRepository users, PasswordEncoder enc) {
		return args -> {
			if (!users.existsByRole(Role.ADMIN)) {
				var admin = User.builder()
						.username("admin")
						.email("admin@palakendra.app")
						.password(enc.encode("admin@123"))
						.role(Role.ADMIN)
						.active(true)
						.build();
				users.save(admin);
			}
		};
	}


//	@Bean
//	CommandLineRunner printHash(PasswordEncoder enc) {
//		return args -> System.out.println(enc.encode("admin@123"));
//	}
}
