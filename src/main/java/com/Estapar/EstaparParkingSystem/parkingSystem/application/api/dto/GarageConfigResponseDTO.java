package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import java.util.List;

public record GarageConfigResponseDTO(
    Integer capacity,
    List<String> sectors
){}
