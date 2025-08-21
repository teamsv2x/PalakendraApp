package com.palakendra.palakendra.domain.repo;

import com.palakendra.palakendra.domain.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByPhoneAndConsumedFalseAndExpiresAtAfterOrderByIdDesc(String phone, Instant now);
}
