package com.palakendra.palakendra.service;

import com.palakendra.palakendra.domain.entity.enums.Shift;
import com.palakendra.palakendra.domain.repo.MilkEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ReportService {
    private final MilkEntryRepository entries;

    public ReportService(MilkEntryRepository entries) {
        this.entries = entries;
    }

    public BigDecimal sumMonth(Long coId, LocalDate from, LocalDate to) {
        return entries.sumLitersBetween(coId, from, to);
    }

    public BigDecimal sumDay(Long coId, LocalDate day) {
        return entries.sumForDay(coId, day);
    }

    public BigDecimal sumDayShift(Long coId, LocalDate day, Shift shift) {
        return entries.sumForDayAndShift(coId, day, shift);
    }
}
