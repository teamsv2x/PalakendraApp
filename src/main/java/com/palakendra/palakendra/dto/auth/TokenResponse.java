package com.palakendra.palakendra.dto.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {}
