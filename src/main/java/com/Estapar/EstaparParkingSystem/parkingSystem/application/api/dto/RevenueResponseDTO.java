package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.time.Instant;

public record RevenueResponseDTO(
        BigDecimal amount,
        CurrencyEnum currency,
        Instant timestamp
) {}
