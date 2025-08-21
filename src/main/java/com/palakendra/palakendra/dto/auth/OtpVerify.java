package com.palakendra.palakendra.dto.auth;

import jakarta.validation.constraints.NotBlank;
public record OtpVerify(@NotBlank String phone, @NotBlank String code) {}
