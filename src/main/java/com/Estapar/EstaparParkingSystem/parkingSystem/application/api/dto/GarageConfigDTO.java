package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GarageConfigDTO(
        String sector,
        BigDecimal basePrice,

        @JsonProperty("max_capacity")
        int maxCapacity
) {
}
