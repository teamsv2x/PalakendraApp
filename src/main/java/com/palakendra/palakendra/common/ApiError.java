package com.palakendra.palakendra.common;

import java.time.Instant; import java.util.Map;
public record ApiError(Instant timestamp, int status, String error, String message, String path, Map<String,Object> details) {
    //code here
}

