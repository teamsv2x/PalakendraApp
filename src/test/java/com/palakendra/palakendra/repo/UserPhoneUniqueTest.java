package com.palakendra.palakendra.repo;

import com.palakendra.palakendra.config.FlywayH2TestConfig;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(FlywayH2TestConfig.class)
@ActiveProfiles("test")
class UserPhoneUniqueTest {

    @Autowired UserRepository users;

    @Test
    void unique_phone_constraint() {
        users.saveAndFlush(User.builder()
                .username("9000000001")
                .phone("9000000001")
                .password("x")
                .role(Role.CUSTOMER)
                .active(true)
                .build());

        assertThatThrownBy(() ->
                users.saveAndFlush(User.builder()
                        .username("another")
                        .phone("9000000001")
                        .password("x")
                        .role(Role.CUSTOMER)
                        .active(true)
                        .build())
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
