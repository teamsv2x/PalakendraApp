package com.palakendra.palakendra.dto.report;

import java.math.BigDecimal; import java.time.LocalDate;
public record DailyTotalResponse(Long organizationId, Long customerId, LocalDate day, BigDecimal morningLiters, BigDecimal eveningLiters, BigDecimal totalLiters) {}
