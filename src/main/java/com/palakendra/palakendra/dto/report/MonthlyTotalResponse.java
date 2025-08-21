package com.palakendra.palakendra.dto.report;

import java.math.BigDecimal;
public record MonthlyTotalResponse(Long organizationId, Long customerId, int year, int month, BigDecimal totalLiters) {}
