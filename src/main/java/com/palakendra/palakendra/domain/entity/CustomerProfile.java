package com.palakendra.palakendra.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "address")
    private String address; // <-- NEW
}
