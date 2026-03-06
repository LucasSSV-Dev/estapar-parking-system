package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

public record ParkingSpotConfigDTO(
        String sector,
        double lat,
        double lng
) {
}
