package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

public record ParkingSpotConfigDTO(
        int id,
        String sector,
        double lat,
        double lng
) {
}
