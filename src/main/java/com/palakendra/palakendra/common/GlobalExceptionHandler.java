package com.palakendra.palakendra.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant; import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiError> handleValidation(Exception ex, HttpServletRequest req){
        Map<String,Object> details = new HashMap<>();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, HttpServletRequest req){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req.getRequestURI(), null);
    }


    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiError> handleAuthExceptions(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
        return build(org.springframework.http.HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(io.jsonwebtoken.security.WeakKeyException.class)
    public ResponseEntity<ApiError> handleWeakKey(io.jsonwebtoken.security.WeakKeyException ex, jakarta.servlet.http.HttpServletRequest req){
        return build(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "JWT secret too short; use 64+ chars.", req.getRequestURI(), null);
    }


    private ResponseEntity<ApiError> build(HttpStatus status, String msg, String path, Map<String,Object> details){
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), msg, path, details));
    }
}

