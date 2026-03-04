package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GarageConfigDTO(
        String sector,
        double basePrice,

        @JsonProperty("max_capacity")
        int maxCapacity
) {
}
