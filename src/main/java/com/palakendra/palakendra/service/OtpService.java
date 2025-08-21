package com.palakendra.palakendra.service;

import com.palakendra.palakendra.domain.entity.OtpCode;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OtpCodeRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import com.palakendra.palakendra.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpCodeRepository otpRepo;
    private final UserRepository users;
    private final PasswordEncoder enc;
    private final JwtService jwt;

    private static final SecureRandom RNG = new SecureRandom();
    private static final int OTP_TTL_SECONDS = 300; // 5 minutes

    public void requestOtp(String phone) {
        // Ensure this phone belongs to an existing CUSTOMER user
        User u = users.findByUsername(phone)  // we use phone as username for customers
                .or(() -> users.findByPhone(phone))
                .orElseThrow(() -> new RuntimeException("Customer not found for phone: " + phone));
        if (u.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("OTP login is only for customers");
        }

        String code = String.format("%06d", RNG.nextInt(1_000_000));
        String hash = enc.encode(code);

        OtpCode otp = OtpCode.builder()
                .phone(phone)
                .codeHash(hash)
                .expiresAt(Instant.now().plusSeconds(OTP_TTL_SECONDS))
                .consumed(false)
                .build();
        otpRepo.save(otp);

        // TODO: integrate SMS provider. For now, print to logs for dev:
        System.out.println("📲 OTP for " + phone + " = " + code + " (valid 5 min)");
    }

    public String verifyOtp(String phone, String code) {
        OtpCode otp = otpRepo.findTopByPhoneAndConsumedFalseAndExpiresAtAfterOrderByIdDesc(phone, Instant.now())
                .orElseThrow(() -> new RuntimeException("OTP not found or expired"));

        if (!enc.matches(code, otp.getCodeHash())) {
            throw new RuntimeException("Invalid OTP code");
        }
        otp.setConsumed(true);
        otpRepo.save(otp);

        User u = users.findByUsername(phone)
                .or(() -> users.findByPhone(phone))
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return jwt.generate(u.getUsername(), Map.of("role", u.getRole().name(), "uid", u.getId()));
    }
}
