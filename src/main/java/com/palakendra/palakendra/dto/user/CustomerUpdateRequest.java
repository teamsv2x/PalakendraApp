package com.palakendra.palakendra.dto.user;

import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank String fullName,
        String address,
        @NotBlank String phone   // manager can change the customer's phone
) {}