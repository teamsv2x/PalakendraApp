package com.palakendra.palakendra.repo;

import com.palakendra.palakendra.config.FlywayH2TestConfig;
import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OrganizationRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(FlywayH2TestConfig.class)
@ActiveProfiles("test")
class OrganizationRepositoryTest {

    @Autowired OrganizationRepository orgs;
    @Autowired UserRepository users;

    @Test
    void findByManager() {
        var m = users.save(User.builder().username("mgrA").email("m@e")
                .password("x") // <-- add this
                .role(Role.MANAGER).active(true).build());
        orgs.save(Organization.builder().name("Org A").address("Street 1").manager(m).build());
        assertThat(orgs.findByManager(m)).isPresent();
    }
}
