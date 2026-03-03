package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GarageImportService {

    private final RestTemplate restTemplate;
    private final GarageService garageService;

    @Value("${simulator.url}")
    private String simulatorUrl;

    public void importFromSimulator() {
        GarageConfigResponseDTO response =
                restTemplate.getForObject(
                        simulatorUrl + "/garage",
                        GarageConfigResponseDTO.class
                );

        garageService.importGarageConfig(response);
    }
}
