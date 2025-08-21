package com.palakendra.palakendra.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "otp_codes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "code_hash", nullable = false, length = 255)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean consumed = false;

    // IMPORTANT: Default value is ignored by @Builder unless we mark it with @Builder.Default.
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        // (optional safety) ensure expiresAt isn’t null
        if (expiresAt == null) expiresAt = Instant.now().plusSeconds(300);
    }
}
