package com.palakendra.palakendra.dto.auth;

import jakarta.validation.constraints.*;
public record ManagerSelfRegisterRequest(
        @NotBlank String managerName,
        @NotBlank String username,
        @Email(message = "invalid email") String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String organizationName,
        String address
) {}
