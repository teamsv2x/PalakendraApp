package com.palakendra.palakendra.web;

import com.palakendra.palakendra.common.ApiResponse;
import com.palakendra.palakendra.domain.entity.Organization;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OrganizationRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import com.palakendra.palakendra.dto.auth.*;
import com.palakendra.palakendra.dto.user.ManagerResponse;
import com.palakendra.palakendra.security.JwtService;
import com.palakendra.palakendra.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final UserRepository users;
    private final OrganizationRepository orgs;
    private final PasswordEncoder encoder;
    private final OtpService otpService;

    // ---------- LOGIN ----------
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest req) {
        // Will throw BadCredentialsException if wrong, which your GlobalExceptionHandler maps to 401
        String id = req.username().trim();
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(id, req.password())
        );

        var user = users.findByUsername(req.username()).orElseThrow();
        String token = jwt.generate(
                user.getUsername(),
                Map.of("role", user.getRole().name(), "uid", user.getId())
        );

        // If you want to reflect your config expiration, you could expose it from JwtService
        return ResponseEntity.ok(
                ApiResponse.ok(new TokenResponse(token, "Bearer", 60L * 60))
        );
    }


    @PostMapping("/otp/request")
    public ResponseEntity<ApiResponse<Map<String,String>>> otpRequest(@Valid @RequestBody OtpRequest req) {
        otpService.requestOtp(req.phone());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status","OTP sent (dev: check server logs)")));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<TokenResponse>> otpVerify(@Valid @RequestBody OtpVerify req) {
        String token = otpService.verifyOtp(req.phone(), req.code());
        return ResponseEntity.ok(ApiResponse.ok(new TokenResponse(token, "Bearer", 60L*60)));
    }

    // ---------- MANAGER SELF-REGISTER ----------
    @PostMapping("/register/manager")
    @Transactional
    public ResponseEntity<ApiResponse<ManagerResponse>> managerSelfRegister(
            @Valid @RequestBody ManagerSelfRegisterRequest req) {

        // 1) Prevent duplicate usernames
        if (users.findByUsername(req.username()).isPresent()) {
            throw new RuntimeException("username already exists");
        }

        // 2) Create MANAGER user
        User manager = User.builder()
                .username(req.username())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .role(Role.MANAGER)
                .active(true)
                .build();
        users.save(manager); // ensures manager has an ID

        // 3) Create and SAVE Organization (use the returned entity so ID is populated)
        Organization saved = orgs.save(
                Organization.builder()
                        .name(req.organizationName())
                        .address(req.address())
                        .manager(manager)
                        .build()
        );

        // 4) Response with generated organizationId
        ManagerResponse dto = new ManagerResponse(
                manager.getId(),
                saved.getId(),               // <-- not null
                manager.getUsername(),
                saved.getName()
        );
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(org.springframework.security.core.Authentication auth) {
        if (auth == null) {
            // 401 is clearer than a NullPointerException

            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false,
                            java.util.Map.of("error", "No authentication"),
                            "Unauthorized"));
        }
        return ResponseEntity.ok(
                ApiResponse.ok(java.util.Map.of(
                        "username", auth.getName(),
                        "authorities", auth.getAuthorities().toString()
                ))
        );
    }
}
