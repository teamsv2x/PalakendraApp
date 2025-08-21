package com.palakendra.palakendra.domain.entity;

import com.palakendra.palakendra.domain.entity.enums.CustomerOrgStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "customer_org",
        uniqueConstraints = @UniqueConstraint(name = "uk_customer_org", columnNames = {"customer_id","organization_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerOrganization {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CustomerOrgStatus status = CustomerOrgStatus.ACTIVE;

    @Column(name = "blocked_at")
    private Instant blockedAt;

    @Column(name = "block_note")
    private String blockNote;
}
