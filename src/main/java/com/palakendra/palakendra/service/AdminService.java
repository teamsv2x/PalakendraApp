package com.palakendra.palakendra.service;

import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OrganizationRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final UserRepository users;
    private final OrganizationRepository orgs;
    private final PasswordEncoder enc;

    public AdminService(UserRepository users, OrganizationRepository orgs, PasswordEncoder enc) {
        this.users = users;
        this.orgs = orgs;
        this.enc = enc;
    }

    @Transactional
    public Organization createManagerAndOrg(String username, String email, String password, String orgName, String address) {
        User manager = User.builder()
                .username(username)
                .email(email)
                .password(enc.encode(password))
                .role(Role.MANAGER)
                .active(true)
                .build();
        users.save(manager);

        Organization org = Organization.builder()
                .name(orgName)
                .address(address)
                .manager(manager)
                .build();
        return orgs.save(org);
    }
}
