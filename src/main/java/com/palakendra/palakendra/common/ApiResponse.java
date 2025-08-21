package com.palakendra.palakendra.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, T data, String message) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, data, null); }
    public static ApiResponse<Void> ok() { return new ApiResponse<>(true, null, null); }
    public static <T> ApiResponse<T> fail(String message) { return new ApiResponse<>(false, null, message); }
}