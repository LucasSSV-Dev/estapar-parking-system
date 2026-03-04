package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import java.util.List;

public record GarageConfigRequestDTO(
        List<GarageConfigDTO> garage,
        List<ParkingSpotConfigDTO> spots
) {}