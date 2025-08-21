package com.palakendra.palakendra.service;

import com.palakendra.palakendra.common.BusinessException;
import com.palakendra.palakendra.domain.entity.*;
import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ManagerService {
    private final UserRepository users;
    private final OrganizationRepository orgs;
    private final CustomerProfileRepository customers;
    private final CustomerOrganizationRepository links;
    private final MilkEntryRepository milk;

    public ManagerService(UserRepository users,
                          OrganizationRepository orgs,
                          CustomerProfileRepository customers,
                          CustomerOrganizationRepository links,
                          MilkEntryRepository milk) {
        this.users = users;
        this.orgs = orgs;
        this.customers = customers;
        this.links = links;
        this.milk = milk;
    }

    private Organization myOrg(String managerUsername) {
        User me = users.findByUsername(managerUsername)
                .orElseThrow(() -> new BusinessException("Manager not found: " + managerUsername));
        if (me.getRole() != Role.MANAGER || !me.isActive()) {
            throw new BusinessException("Only active MANAGER can perform this action");
        }
        return orgs.findByManager(me)
                .orElseThrow(() -> new BusinessException("Manager has no organization"));
    }

    /** CREATE or LINK customer to this manager's org (profile is upserted). */
    @Transactional
    public CustomerProfile createOrLinkCustomerToOrg(String managerUsername, String fullName, String phone, String address) {
        Organization org = myOrg(managerUsername);

        // Find or create the USER (username == phone; OTP flow so password nullable)
        User u = users.findByUsername(phone).orElse(null);
        if (u == null) {
            if (users.existsByPhone(phone)) {
                throw new BusinessException("Phone already in use");
            }
            u = User.builder()
                    .username(phone)
                    .phone(phone)
                    .password(null)  // OTP login
                    .role(Role.CUSTOMER)
                    .active(true)
                    .build();
            u = users.save(u);
        }

        // Upsert the PROFILE (no orElseThrow here)
        User finalU = u;
        CustomerProfile cp = customers.findByUser(u).orElseGet(() ->
                customers.save(CustomerProfile.builder()
                        .user(finalU)
                        .fullName(fullName)
                        .address(address)
                        .build())
        );

        // Prevent duplicate ORG link
        if (links.findByCustomerIdAndOrganizationId(cp.getId(), org.getId()).isPresent()) {
            throw new BusinessException("Customer already linked to this organization");
        }

        links.save(CustomerOrganization.builder()
                .customer(cp)
                .organization(org)
                .status(CustomerOrgStatus.ACTIVE)
                .build());

        return cp;
    }

    /** LIST by status (ACTIVE or BLOCKED). */
    public List<CustomerOrganization> listMyCustomers(String managerUsername, CustomerOrgStatus status) {
        Organization org = myOrg(managerUsername);
        if (status == null) return links.findAllByOrganization(org);
        return links.findAllByOrganizationAndStatus(org, status);
    }

    /** UPDATE fullName/address/phone (phone is unique & used for OTP login). */
    @Transactional
    public CustomerProfile updateCustomerInMyOrg(String managerUsername, Long customerId, String fullName, String address, String phone) {
        Organization org = myOrg(managerUsername);
        CustomerProfile cp = customers.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer profile not found: " + customerId));

        links.findByCustomerIdAndOrganizationId(cp.getId(), org.getId())
                .orElseThrow(() -> new BusinessException("Customer not in your organization"));

        // If phone changed -> enforce uniqueness and update both username and phone on User
        User u = cp.getUser();
        if (phone != null && !phone.equals(u.getPhone())) {
            if (users.existsByPhone(phone)) throw new BusinessException("Phone already in use");
            u.setPhone(phone);
            u.setUsername(phone); // OTP login uses phone as username
            users.save(u);
        }

        if (fullName != null) cp.setFullName(fullName);
        if (address != null)  cp.setAddress(address);
        return customers.save(cp);
    }

    /** BLOCK customer in this org (soft stop). */
    @Transactional
    public void blockCustomerInMyOrg(String managerUsername, Long customerId, String note) {
        Organization org = myOrg(managerUsername);
        CustomerProfile cp = customers.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer profile not found: " + customerId));

        CustomerOrganization link = links.findByCustomerIdAndOrganizationId(cp.getId(), org.getId())
                .orElseThrow(() -> new BusinessException("Customer not in your organization"));

        link.setStatus(CustomerOrgStatus.BLOCKED);
        link.setBlockedAt(Instant.now());
        link.setBlockNote(note);
        links.save(link);
    }

    /** UNBLOCK (reactivate) customer in this org. */
    @Transactional
    public void unblockCustomerInMyOrg(String managerUsername, Long customerId) {
        Organization org = myOrg(managerUsername);
        CustomerProfile cp = customers.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer profile not found: " + customerId));

        CustomerOrganization link = links.findByCustomerIdAndOrganizationId(cp.getId(), org.getId())
                .orElseThrow(() -> new BusinessException("Customer not in your organization"));

        link.setStatus(CustomerOrgStatus.ACTIVE);
        link.setBlockedAt(null);
        link.setBlockNote(null);
        links.save(link);
    }

    /** (Optional) HARD unlink — not recommended if you want to keep history. */
    @Transactional
    public void unlinkCustomerFromMyOrg(String managerUsername, Long customerId) {
        Organization org = myOrg(managerUsername);
        CustomerProfile cp = customers.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer profile not found: " + customerId));

        CustomerOrganization link = links.findByCustomerIdAndOrganizationId(cp.getId(), org.getId())
                .orElseThrow(() -> new BusinessException("Customer not in your organization"));

        // keep milk history -> do NOT delete milk entries
        links.delete(link);

        if (links.countByCustomer(cp) == 0) {
            // Optionally remove profile/user if totally unused elsewhere
            // customers.delete(cp);
            // users.delete(cp.getUser());
        }
    }
}
