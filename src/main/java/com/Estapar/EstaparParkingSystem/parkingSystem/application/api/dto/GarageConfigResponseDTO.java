package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class GarageConfigResponseDTO {
    private Integer capacity;
    private List<String> sectors;
}
