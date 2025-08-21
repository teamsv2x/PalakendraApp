package com.palakendra.palakendra.dto.user;

public record CustomerResponse(Long customerId,
                               String fullName,
                               String phone,
                               String address) {}
