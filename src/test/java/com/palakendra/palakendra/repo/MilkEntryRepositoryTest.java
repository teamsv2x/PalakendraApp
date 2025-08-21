package com.palakendra.palakendra.repo;

import com.palakendra.palakendra.config.FlywayH2TestConfig;
import com.palakendra.palakendra.domain.entity.*;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.entity.enums.Shift;
import com.palakendra.palakendra.domain.repo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(FlywayH2TestConfig.class)
@ActiveProfiles("test")
class MilkEntryRepositoryTest {

    @Autowired UserRepository users;
    @Autowired OrganizationRepository orgs;
    @Autowired CustomerProfileRepository customers;
    @Autowired CustomerOrganizationRepository links;
    @Autowired MilkEntryRepository milk;

    @Test
    void sums_daily_and_month_range_and_uniqueConstraint() {
        var mgr = users.save(User.builder().username("mgrA").role(Role.MANAGER)
                .password("x") // <-- add this
                .active(true).build());
        var org = orgs.save(Organization.builder().name("Org A").manager(mgr).build());
        var cu  = users.save(User.builder().username("9000000001").phone("9000000001")
                .password("x") // <-- add this
                .role(Role.CUSTOMER).active(true).build());
        var cp  = customers.save(CustomerProfile.builder().user(cu).fullName("Ravi").address("Village").build());
        var link = links.save(CustomerOrganization.builder().customer(cp).organization(org).build());

        LocalDate d = LocalDate.of(2025, 8, 17);

        milk.save(MilkEntry.builder().customerOrg(link).date(d).shift(Shift.MORNING).liters(new BigDecimal("7.5")).build());
        milk.save(MilkEntry.builder().customerOrg(link).date(d).shift(Shift.EVENING).liters(new BigDecimal("5.0")).build());

        assertThat(milk.sumForDay(link.getId(), d)).isEqualByComparingTo("12.5");
        assertThat(milk.sumForDayAndShift(link.getId(), d, Shift.MORNING)).isEqualByComparingTo("7.5");
        assertThat(milk.sumLitersBetween(link.getId(), d.withDayOfMonth(1), d.withDayOfMonth(31))).isEqualByComparingTo("12.5");

        assertThatThrownBy(() ->
                milk.save(MilkEntry.builder().customerOrg(link).date(d).shift(Shift.MORNING).liters(new BigDecimal("1.0")).build())
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
