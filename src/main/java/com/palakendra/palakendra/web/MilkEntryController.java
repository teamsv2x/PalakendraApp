package com.palakendra.palakendra.web;

import com.palakendra.palakendra.common.ApiResponse;
import com.palakendra.palakendra.domain.entity.MilkEntry;
import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.repo.CustomerProfileRepository;
import com.palakendra.palakendra.domain.repo.CustomerOrganizationRepository;
import com.palakendra.palakendra.domain.repo.MilkEntryRepository;
import com.palakendra.palakendra.domain.repo.OrganizationRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import com.palakendra.palakendra.dto.milk.MilkEntryRequest;
import com.palakendra.palakendra.dto.milk.MilkEntryResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/milk")
@PreAuthorize("hasRole('MANAGER')")
public class MilkEntryController {
    private final UserRepository users; private final OrganizationRepository orgs; private final CustomerProfileRepository customers; private final CustomerOrganizationRepository links; private final MilkEntryRepository entries;
    public MilkEntryController(UserRepository users, OrganizationRepository orgs, CustomerProfileRepository customers, CustomerOrganizationRepository links, MilkEntryRepository entries){this.users=users; this.orgs=orgs; this.customers=customers; this.links=links; this.entries=entries;}

    private Organization myOrg(Authentication auth){
        var me = users.findByUsername(auth.getName()).orElseThrow();
        return orgs.findByManager(me).orElseThrow();
    }

    @PostMapping
    public ApiResponse<MilkEntryResponse> add(@Valid @RequestBody MilkEntryRequest req, Authentication auth){
        var org = myOrg(auth);
        var cp = customers.findById(req.customerId()).orElseThrow();
        var link = links.findByCustomerIdAndOrganizationId(cp.getId(), org.getId()).orElseThrow();
        var saved = entries.save(MilkEntry.builder().customerOrg(link).date(req.date()).shift(req.shift()).liters(req.liters()).build());
        return ApiResponse.ok(new MilkEntryResponse(saved.getId(), cp.getId(), org.getId(), saved.getDate(), saved.getShift(), saved.getLiters()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication auth){
        // Optional: verify the entry belongs to this manager's org before delete
        entries.deleteById(id);
        return ApiResponse.ok();
    }
}
