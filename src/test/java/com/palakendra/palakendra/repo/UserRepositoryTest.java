package com.palakendra.palakendra.repo;

import com.palakendra.palakendra.config.FlywayH2TestConfig;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(FlywayH2TestConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired UserRepository users;

    @Test
    void saveAndFindByUsername() {
        String uname = "u_" + UUID.randomUUID(); // ensure uniqueness per run
        var u = User.builder()
                .username(uname)
                .email(uname + "@example.com")
                .password("pw")
                .role(Role.CUSTOMER)
                .active(true)
                .build();
        users.save(u);
        assertThat(users.findByUsername("admin")).isPresent();
    }

    @Test
    void existsByPhone() {
        var u = User.builder().username("9000000001").phone("9000000001")
                .password("x") // <-- add this
                .role(Role.CUSTOMER).active(true).build();
        users.save(u);
        assertThat(users.existsByPhone("9000000001")).isTrue();
    }
}
