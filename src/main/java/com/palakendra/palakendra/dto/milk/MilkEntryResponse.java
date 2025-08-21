package com.palakendra.palakendra.dto.milk;

import com.palakendra.palakendra.domain.entity.enums.Shift;

import java.math.BigDecimal; import java.time.LocalDate;
public record MilkEntryResponse(Long id, Long customerId, Long organizationId, LocalDate date, Shift shift, BigDecimal liters) {}
