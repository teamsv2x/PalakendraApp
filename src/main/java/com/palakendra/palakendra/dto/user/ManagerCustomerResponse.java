package com.palakendra.palakendra.dto.user;

import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import java.time.Instant;

public record ManagerCustomerResponse(
        Long customerId,
        String fullName,
        String phone,
        String address,
        CustomerOrgStatus status,
        Instant blockedAt,
        String blockNote
) {}
