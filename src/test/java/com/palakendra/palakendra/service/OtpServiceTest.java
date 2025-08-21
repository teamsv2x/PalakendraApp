package com.palakendra.palakendra.service;

import com.palakendra.palakendra.domain.entity.OtpCode;
import com.palakendra.palakendra.domain.entity.User;
import com.palakendra.palakendra.domain.entity.enums.Role;
import com.palakendra.palakendra.domain.repo.OtpCodeRepository;
import com.palakendra.palakendra.domain.repo.UserRepository;
import com.palakendra.palakendra.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    OtpCodeRepository otpRepo = mock(OtpCodeRepository.class);
    UserRepository users = mock(UserRepository.class);
    BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    JwtService jwt = new JwtService("this-is-a-long-64plus-char-secret-for-tests-1234567890-abcdef-xyz", 60);

    @Test
    void requestOtp_savesHashedCode() {
        var u = User.builder().id(1L).username("9000000001").phone("9000000001").role(Role.CUSTOMER).active(true).build();
        when(users.findByUsername("9000000001")).thenReturn(Optional.of(u));

        var svc = new OtpService(otpRepo, users, enc, jwt);
        svc.requestOtp("9000000001");

        var capt = ArgumentCaptor.forClass(OtpCode.class);
        verify(otpRepo).save(capt.capture());
        assertThat(capt.getValue().getCodeHash()).startsWith("$2a$");
        assertThat(capt.getValue().getExpiresAt()).isAfter(Instant.now().minusSeconds(1));
    }

    @Test
    void verifyOtp_checksHashAndReturnsToken() {
        var u = User.builder().id(1L).username("9000000001").phone("9000000001").role(Role.CUSTOMER).active(true).build();
        when(users.findByUsername("9000000001")).thenReturn(Optional.of(u));

        var codeHash = enc.encode("123456");
        var otp = OtpCode.builder().id(1L).phone("9000000001").codeHash(codeHash)
                .expiresAt(Instant.now().plusSeconds(300)).consumed(false).build();
        when(otpRepo.findTopByPhoneAndConsumedFalseAndExpiresAtAfterOrderByIdDesc(eq("9000000001"), any()))
                .thenReturn(Optional.of(otp));

        var svc = new OtpService(otpRepo, users, enc, jwt);
        String token = svc.verifyOtp("9000000001", "123456");

        assertThat(token).contains(".");
        assertThat(otp.isConsumed()).isTrue();
        verify(otpRepo).save(otp);
    }
}
