package com.palakendra.palakendra.service;

import com.palakendra.palakendra.common.BusinessException;
import com.palakendra.palakendra.domain.entity.*;
import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManagerServiceTest {

    UserRepository users = mock(UserRepository.class);
    OrganizationRepository orgs = mock(OrganizationRepository.class);
    CustomerProfileRepository customers = mock(CustomerProfileRepository.class);
    CustomerOrganizationRepository links = mock(CustomerOrganizationRepository.class);
    MilkEntryRepository milk = mock(MilkEntryRepository.class);

    ManagerService svc;

    @BeforeEach
    void setup() {
        svc = new ManagerService(users, orgs, customers, links, milk);

        var mgr = User.builder().id(10L).username("mgrA").role(Role.MANAGER).active(true).build();
        var org = Organization.builder().id(20L).name("Org A").manager(mgr).build();

        when(users.findByUsername("mgrA")).thenReturn(Optional.of(mgr));
        when(orgs.findByManager(mgr)).thenReturn(Optional.of(org));
    }

    @Test
    void createOrLink_newCustomer_createsUserProfileAndLink() {
        when(users.findByUsername("9000000001")).thenReturn(Optional.empty());
        when(users.existsByPhone("9000000001")).thenReturn(false);

        var savedUser = User.builder()
                .id(1L).username("9000000001").phone("9000000001")
                .role(Role.CUSTOMER).active(true).build();
        when(users.save(any(User.class))).thenReturn(savedUser);

        when(customers.findByUser(any(User.class))).thenReturn(Optional.empty());

        var savedProfile = CustomerProfile.builder()
                .id(2L).user(savedUser).fullName("Ravi").address("Village A").build();
        when(customers.save(any(CustomerProfile.class))).thenReturn(savedProfile);

        when(links.findByCustomerIdAndOrganizationId(2L, 20L)).thenReturn(Optional.empty());
        when(links.save(any(CustomerOrganization.class))).thenAnswer(inv -> inv.getArgument(0));

        var cp = svc.createOrLinkCustomerToOrg("mgrA", "Ravi", "9000000001", "Village A");

        assertThat(cp.getId()).isEqualTo(2L);
        assertThat(cp.getFullName()).isEqualTo("Ravi");
        verify(users).save(any(User.class));
        verify(customers).save(any(CustomerProfile.class));
        verify(links).save(any(CustomerOrganization.class));
    }

    @Test
    void createOrLink_existingProfile_linksOnly() {
        var existingUser = User.builder()
                .id(1L).username("9000000001").phone("9000000001")
                .role(Role.CUSTOMER).active(true).build();
        when(users.findByUsername("9000000001")).thenReturn(Optional.of(existingUser));

        var existingProfile = CustomerProfile.builder()
                .id(2L).user(existingUser).fullName("Ravi").address("Village A").build();
        when(customers.findByUser(existingUser)).thenReturn(Optional.of(existingProfile));

        when(links.findByCustomerIdAndOrganizationId(2L, 20L)).thenReturn(Optional.empty());
        when(links.save(any(CustomerOrganization.class))).thenAnswer(inv -> inv.getArgument(0));

        var cp = svc.createOrLinkCustomerToOrg("mgrA", "Ravi", "9000000001", "Village A");

        assertThat(cp.getId()).isEqualTo(2L);
        verify(customers, never()).save(any(CustomerProfile.class));
        verify(links).save(any(CustomerOrganization.class));
    }

    @Test
    void updateCustomer_changePhone_updatesUserUsernameAndPhone() {
        var u = User.builder().id(1L).username("9000000001").phone("9000000001")
                .role(Role.CUSTOMER).active(true).build();
        var cp = CustomerProfile.builder().id(2L).user(u).fullName("Ravi").address("Village A").build();

        when(customers.findById(2L)).thenReturn(Optional.of(cp));
        when(users.existsByPhone("9000000022")).thenReturn(false);
        when(links.findByCustomerIdAndOrganizationId(2L, 20L)).thenReturn(Optional.of(new CustomerOrganization()));

        svc.updateCustomerInMyOrg("mgrA", 2L, "Ravi Kumar", "Village B", "9000000022");

        assertThat(u.getPhone()).isEqualTo("9000000022");
        assertThat(u.getUsername()).isEqualTo("9000000022");
        assertThat(cp.getAddress()).isEqualTo("Village B");
        verify(users).save(u);
        verify(customers).save(cp);
    }

    @Test
    void updateCustomer_duplicatePhone_throws() {
        // existing user & profile
        var u = User.builder().id(1L).username("9000000001").phone("9000000001")
                .role(Role.CUSTOMER).active(true).build();
        var cp = CustomerProfile.builder().id(2L).user(u).build();

        when(customers.findById(2L)).thenReturn(Optional.of(cp));
        when(links.findByCustomerIdAndOrganizationId(2L, 20L)).thenReturn(Optional.of(new CustomerOrganization()));

        // try to change to a DIFFERENT phone that is already taken
        when(users.existsByPhone("9000000022")).thenReturn(true);

        assertThatThrownBy(() -> svc.updateCustomerInMyOrg("mgrA", 2L, "Ravi", "Addr", "9000000022"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Phone already in use");

        // ensure we didn't persist any phone change
        verify(users, never()).save(any(User.class));
    }

    @Test
    void updateCustomer_samePhone_ok_noUniquenessCheck() {
        var u = User.builder().id(1L).username("9000000001").phone("9000000001")
                .role(Role.CUSTOMER).active(true).build();
        var cp = CustomerProfile.builder().id(2L).user(u).fullName("Ravi").address("Village A").build();

        when(customers.findById(2L)).thenReturn(Optional.of(cp));
        when(links.findByCustomerIdAndOrganizationId(2L, 20L)).thenReturn(Optional.of(new CustomerOrganization()));

        // same phone -> no call to users.existsByPhone(), no exception
        svc.updateCustomerInMyOrg("mgrA", 2L, "Ravi", "Addr", "9000000001");

        verify(users, never()).existsByPhone(anyString());
        verify(users, never()).save(any(User.class)); // phone unchanged, we don't save user
        verify(customers).save(cp); // profile still saved (name/address)
    }
}
