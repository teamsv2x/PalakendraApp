package com.palakendra.palakendra.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "organizations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Organization {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @OneToOne(optional = false)
    @JoinColumn(name = "manager_user_id", nullable = false, unique = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User manager; // must have role MANAGER
}
