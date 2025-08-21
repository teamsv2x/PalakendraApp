package com.palakendra.palakendra.dto.milk;

import com.palakendra.palakendra.domain.entity.enums.Shift;
import jakarta.validation.constraints.*;
import java.math.BigDecimal; import java.time.LocalDate;
public record MilkEntryRequest(
        @NotNull Long customerId,
        @NotNull LocalDate date,
        @NotNull Shift shift,
        @NotNull @DecimalMin("0.00") BigDecimal liters
) {}
