package com.palakendra.palakendra.web;

import com.palakendra.palakendra.common.ApiResponse;
import com.palakendra.palakendra.domain.entity.CustomerOrganization;
import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import com.palakendra.palakendra.dto.user.*;
import com.palakendra.palakendra.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/manager", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping(value = "/customers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CustomerResponse> create(@AuthenticationPrincipal UserDetails me,
                                                @Valid @RequestBody CustomerCreateRequest req) {
        var cp = managerService.createOrLinkCustomerToOrg(me.getUsername(), req.fullName(), req.phone(), req.address());
        return ApiResponse.ok(new CustomerResponse(cp.getId(), cp.getFullName(), cp.getUser().getPhone(), cp.getAddress()));
    }

    @GetMapping("/customers")
    public ApiResponse<List<ManagerCustomerResponse>> list(@AuthenticationPrincipal UserDetails me,
                                                           @RequestParam(required = false, defaultValue = "ACTIVE") CustomerOrgStatus status) {
        List<CustomerOrganization> list = managerService.listMyCustomers(me.getUsername(), status);
        var dto = list.stream()
                .map(co -> new ManagerCustomerResponse(
                        co.getCustomer().getId(),
                        co.getCustomer().getFullName(),
                        co.getCustomer().getUser().getPhone(),
                        co.getCustomer().getAddress(),
                        co.getStatus(),
                        co.getBlockedAt(),
                        co.getBlockNote()
                ))
                .toList();
        return ApiResponse.ok(dto);
    }

    @PutMapping(value = "/customers/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<CustomerResponse> update(@AuthenticationPrincipal UserDetails me,
                                                @PathVariable Long customerId,
                                                @Valid @RequestBody CustomerUpdateRequest req) {
        var cp = managerService.updateCustomerInMyOrg(me.getUsername(), customerId, req.fullName(), req.address(), req.phone());
        return ApiResponse.ok(new CustomerResponse(cp.getId(), cp.getFullName(), cp.getUser().getPhone(), cp.getAddress()));
    }

    @PatchMapping(value = "/customers/{customerId}/block", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Void> block(@AuthenticationPrincipal UserDetails me,
                                   @PathVariable Long customerId,
                                   @RequestBody(required = false) CustomerBlockRequest req) {
        managerService.blockCustomerInMyOrg(me.getUsername(), customerId, req == null ? null : req.note());
        return ApiResponse.ok(null);
    }

    @PatchMapping("/customers/{customerId}/unblock")
    public ApiResponse<Void> unblock(@AuthenticationPrincipal UserDetails me,
                                     @PathVariable Long customerId) {
        managerService.unblockCustomerInMyOrg(me.getUsername(), customerId);
        return ApiResponse.ok(null);
    }
}
