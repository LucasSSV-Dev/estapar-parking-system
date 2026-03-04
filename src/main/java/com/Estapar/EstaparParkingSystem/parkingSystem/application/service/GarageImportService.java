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
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GarageImportService {

    private final RestClient restClient;
    private final GarageRepository garageRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    private static final String GARAGE_URL = "http://localhost:3000/garage";

    @Transactional
    public void importGarage() {

        GarageConfigRequestDTO requestDTO =
                restClient.get()
                        .uri(GARAGE_URL)
                        .retrieve()
                        .body(GarageConfigRequestDTO.class);

        if (requestDTO == null) {
            throw new IllegalStateException("Garage config not received");
        }

        saveGarages(requestDTO.garage());
        saveSpots(requestDTO.spots());
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
    }



    private void saveSpots(List<ParkingSpotConfigDTO> spots) {
        List<ParkingSpot> entities = spots.stream()
                .map(spotConfigDTO -> {
                    ParkingSpot s = new ParkingSpot();
                    s.setSector(spotConfigDTO.sector());
                    s.setLatitude(spotConfigDTO.lat());
                    s.setLongitude(spotConfigDTO.lng());
                    s.setOccupied(false);
                    s.setGarage(getGarage(spotConfigDTO));

                    return s;
                })
                .toList();

        parkingSpotRepository.saveAll(entities);
    }


    private @NonNull Garage getGarage(ParkingSpotConfigDTO dto) {
        return garageRepository
                .findBySector(dto.sector())
                .orElseThrow(() -> new IllegalStateException("Garage not found"));
    }
}
