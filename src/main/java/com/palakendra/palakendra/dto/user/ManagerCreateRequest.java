package com.palakendra.palakendra.dto.user;

import jakarta.validation.constraints.*;
public record ManagerCreateRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String organizationName,
        String address
) {}
