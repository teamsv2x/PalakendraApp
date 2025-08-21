package com.palakendra.palakendra.dto.user;

import jakarta.validation.constraints.*;
public record CustomerCreateRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        String address
) {}
