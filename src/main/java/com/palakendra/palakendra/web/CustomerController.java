package com.palakendra.palakendra.web;

import com.palakendra.palakendra.common.ApiResponse;
import com.palakendra.palakendra.domain.repo.CustomerOrganizationRepository;
import com.palakendra.palakendra.domain.repo.CustomerProfileRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {
    private final UserRepository users; private final CustomerProfileRepository customers; private final CustomerOrganizationRepository links;
    public CustomerController(UserRepository users, CustomerProfileRepository customers, CustomerOrganizationRepository links){this.users=users; this.customers=customers; this.links=links;}

    @GetMapping("/organizations")
    public ApiResponse<?> myOrganizations(Authentication auth){
        var me = users.findByUsername(auth.getName()).orElseThrow();
        var cp = customers.findByUser(me).orElseThrow();
        var orgs = links.findAllByCustomer(cp).stream().map(l -> l.getOrganization()).toList();
        return ApiResponse.ok(orgs);
    }
}
