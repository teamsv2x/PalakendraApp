package com.palakendra.palakendra.repo;

import com.palakendra.palakendra.config.FlywayH2TestConfig;
import com.palakendra.palakendra.domain.entity.*;
import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(FlywayH2TestConfig.class)
@ActiveProfiles("test")
class CustomerOrganizationRepositoryTest {

    @Autowired UserRepository users;
    @Autowired CustomerProfileRepository customers;
    @Autowired OrganizationRepository orgs;
    @Autowired CustomerOrganizationRepository links;

    @Test
    void linkAndFilterByStatus() {
        var mgr = users.save(User.builder().username("mgrA").role(Role.MANAGER)
                .password("x") // <-- add this
                .active(true).build());
        var org = orgs.save(Organization.builder().name("Org A").manager(mgr).build());
        var cu  = users.save(User.builder().username("9000000001").phone("9000000001")
                .password("x") // <-- add this
                .role(Role.CUSTOMER).active(true).build());
        var cp  = customers.save(CustomerProfile.builder().user(cu).fullName("Ravi").build());

        var link = links.save(CustomerOrganization.builder().customer(cp).organization(org).build());
        assertThat(links.findAllByOrganization(org)).hasSize(1);

        link.setStatus(CustomerOrgStatus.BLOCKED);
        links.save(link);

        assertThat(links.findAllByOrganizationAndStatus(org, CustomerOrgStatus.BLOCKED)).hasSize(1);
    }
}
