package com.Estapar.EstaparParkingSystem.parkingSystem.application.infra.config;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.service.GarageImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GarageLayoutGetterConfig implements CommandLineRunner {

    private final GarageImportService garageImportService;

    @Override
    public void run(String... args) {
        garageImportService.importGarage();
    }
}
