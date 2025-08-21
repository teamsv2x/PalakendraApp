package com.palakendra.palakendra.web;

import com.palakendra.palakendra.common.ApiResponse;
import com.palakendra.palakendra.domain.entity.enums.Shift;
import com.palakendra.palakendra.domain.repo.*;
import com.palakendra.palakendra.dto.report.DailyTotalResponse;
import com.palakendra.palakendra.dto.report.MonthlyTotalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.time.*;

@RestController @RequestMapping("/api/reports")
public class ReportController {
    private final UserRepository users; private final OrganizationRepository orgs; private final CustomerProfileRepository customers; private final CustomerOrganizationRepository links; private final MilkEntryRepository entries;
    public ReportController(UserRepository users, OrganizationRepository orgs, CustomerProfileRepository customers, CustomerOrganizationRepository links, MilkEntryRepository entries){this.users=users; this.orgs=orgs; this.customers=customers; this.links=links; this.entries=entries;}

    private Long resolveCustomerOrgId(Long customerId, Long orgId){
        return links.findByCustomerIdAndOrganizationId(customerId, orgId).orElseThrow().getId();
    }

    // Manager view totals for a customer in their org for a day
    @GetMapping("/manager/day") @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<DailyTotalResponse> dayTotal(@RequestParam Long customerId, @RequestParam String day, Authentication auth){
        var org = orgs.findByManager(users.findByUsername(auth.getName()).orElseThrow()).orElseThrow();
        var date = LocalDate.parse(day);
        Long coId = resolveCustomerOrgId(customerId, org.getId());
        BigDecimal m = entries.sumForDayAndShift(coId, date, Shift.MORNING);
        BigDecimal e = entries.sumForDayAndShift(coId, date, Shift.EVENING);
        return ApiResponse.ok(new DailyTotalResponse(org.getId(), customerId, date, m, e, m.add(e)));
    }

    // Monthly total (manager side)
    @GetMapping("/manager/month") @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<MonthlyTotalResponse> monthTotal(@RequestParam Long customerId, @RequestParam int year, @RequestParam int month, Authentication auth){
        var org = orgs.findByManager(users.findByUsername(auth.getName()).orElseThrow()).orElseThrow();
        var from = LocalDate.of(year, month, 1);
        var to = from.withDayOfMonth(from.lengthOfMonth());
        Long coId = resolveCustomerOrgId(customerId, org.getId());
        var sum = entries.sumLitersBetween(coId, from, to);
        return ApiResponse.ok(new MonthlyTotalResponse(org.getId(), customerId, year, month, sum));
    }

    // Customer self view monthly per org
    @GetMapping("/customer/month") @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<MonthlyTotalResponse> customerMonth(@RequestParam Long orgId, @RequestParam int year, @RequestParam int month, Authentication auth){
        var me = users.findByUsername(auth.getName()).orElseThrow();
        var cp = customers.findByUser(me).orElseThrow();
        var from = LocalDate.of(year, month, 1);
        var to = from.withDayOfMonth(from.lengthOfMonth());
        Long coId = resolveCustomerOrgId(cp.getId(), orgId);
        var sum = entries.sumLitersBetween(coId, from, to);
        return ApiResponse.ok(new MonthlyTotalResponse(orgId, cp.getId(), year, month, sum));
    }
}
