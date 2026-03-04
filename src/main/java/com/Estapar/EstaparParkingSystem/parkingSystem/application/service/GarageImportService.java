package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.ParkingSpotConfigDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GarageImportService {

    private final RestClient restClient;
    private final GarageRepository garageRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    @Value("${garage.api.url}")
    private String garageUrl;

    @Transactional
    public void importGarage() {
        log.info("[starts] GarageImportService - importGarage()");

        GarageConfigRequestDTO requestDTO =
                restClient.get()
                        .uri(garageUrl)
                        .retrieve()
                        .body(GarageConfigRequestDTO.class);

        if (requestDTO == null) {
            throw new IllegalStateException("Garage config not received");
        }

        saveGarages(requestDTO.garage());
        saveSpots(requestDTO.spots());
        log.info("[ends] GarageImportService - importGarage()\n");
    }

    private void saveGarages(List<GarageConfigDTO> garages) {
        List<Garage> entities = garages.stream()
                .map(garageConfigDTO -> {
                    Garage garage = new Garage();
                    garage.setSector(garageConfigDTO.sector());
                    garage.setBasePrice(garageConfigDTO.basePrice());
                    garage.setMaxCapacity(garageConfigDTO.maxCapacity());
                    garage.setCurrentOccupancy(0);

                    return garage;
                })
                .toList();

        garageRepository.saveAll(entities);
        //Pra não dar problema de pesquisar a garagem depois e nao achar por não estar no banco ainda...
        garageRepository.flush();
    }



    private void saveSpots(List<ParkingSpotConfigDTO> spots) {
        List<ParkingSpot> entities = spots.stream()
                .map(spotConfigDTO -> {
                    ParkingSpot spot = new ParkingSpot();
                    spot.setSector(spotConfigDTO.sector());
                    spot.setLatitude(spotConfigDTO.lat());
                    spot.setLongitude(spotConfigDTO.lng());
                    spot.setOccupied(false);
                    spot.setGarage(getGarage(spotConfigDTO)); //Ia dar erro aqui

                    return spot;
                })
                .toList();

        parkingSpotRepository.saveAll(entities);
    }


    private @NonNull Garage getGarage(ParkingSpotConfigDTO parkingSpotConfigDTO) {
        return garageRepository
                .findBySector(parkingSpotConfigDTO.sector())
                .orElseThrow(() -> new IllegalStateException("Garage not found"));
    }
}
