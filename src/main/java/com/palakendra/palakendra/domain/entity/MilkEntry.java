package com.palakendra.palakendra.domain.entity;
import com.palakendra.palakendra.domain.entity.enums.Shift;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "milk_entries",
        uniqueConstraints = @UniqueConstraint(name = "uk_entry_unique", columnNames = {"customer_org_id", "entry_date", "shift"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MilkEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "customer_org_id")
    private CustomerOrganization customerOrg;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Shift shift; // MORNING or EVENING

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal liters;
}
