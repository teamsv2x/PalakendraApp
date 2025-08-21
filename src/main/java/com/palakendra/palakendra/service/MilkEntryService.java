package com.palakendra.palakendra.service;

import com.palakendra.palakendra.domain.entity.CustomerOrganization;
import com.palakendra.palakendra.domain.entity.MilkEntry;
import com.palakendra.palakendra.domain.entity.enums.Shift;
import com.palakendra.palakendra.domain.repo.CustomerOrganizationRepository;
import com.palakendra.palakendra.domain.repo.MilkEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MilkEntryService {
    private final MilkEntryRepository entries;
    private final CustomerOrganizationRepository links;

    public MilkEntryService(MilkEntryRepository entries, CustomerOrganizationRepository links) {
        this.entries = entries;
        this.links = links;
    }

    public MilkEntry addEntry(Long customerId, Long orgId, LocalDate date, Shift shift, BigDecimal liters) {
        CustomerOrganization link = links.findByCustomerIdAndOrganizationId(customerId, orgId).orElseThrow();
        MilkEntry entry = MilkEntry.builder()
                .customerOrg(link)
                .date(date)
                .shift(shift)
                .liters(liters)
                .build();
        return entries.save(entry);
    }

    public void delete(Long id) {
        entries.deleteById(id);
    }
}
