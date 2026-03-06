package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.GarageConfigRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.ParkingSpotConfigDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.GarageConfigNotReceivedException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.GarageNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            log.info("[error] GarageImportService - importGarage()\n");
            throw new GarageConfigNotReceivedException("Garage config not received");
        }

        saveGarages(requestDTO.garage());
        saveSpots(requestDTO.spots());
        log.info("[ends] GarageImportService - importGarage()\n");
    }

    private void saveGarages(List<GarageConfigDTO> garages) {
        Map<String, Garage> garageMap = getGarageMap();
        List<Garage> entities = garages.stream()
                .map(dto -> {
                    Garage garage = garageMap.getOrDefault(
                            dto.sector(),
                            new Garage()
                    );
                    garage.setSector(dto.sector());
                    garage.setBasePrice(dto.basePrice());
                    garage.setMaxCapacity(dto.maxCapacity());
                    if (garage.getId() == null) {
                        garage.setCurrentOccupancy(0);
                    }

                    return garage;
                })
                .toList();

        garageRepository.saveAll(entities);
        garageRepository.flush();//Para garantir que a entity exista no banco de dados, se não daria erro abaixo
    }

    private void saveSpots(List<ParkingSpotConfigDTO> spots) {

        List<ParkingSpot> entities = spots.stream()
                .map(spotConfigDTO -> {
                    ParkingSpot spot = parkingSpotRepository
                            .findBySectorAndLatitudeAndLongitude(
                                    spotConfigDTO.sector(),
                                    spotConfigDTO.lat(),
                                    spotConfigDTO.lng())
                            .orElseGet(ParkingSpot::new);
                    spot.setSector(spotConfigDTO.sector());
                    spot.setLatitude(spotConfigDTO.lat());
                    spot.setLongitude(spotConfigDTO.lng());
                    if (spot.getId() == null) {
                        spot.setOccupied(false);
                    }
                    spot.setGarage(getGarage(spotConfigDTO)); //Ia dar erro aqui

                    return spot;
                })
                .toList();

        parkingSpotRepository.saveAll(entities);
    }

    private Map<String, Garage> getGarageMap() { //Tava fazendo a busca toda vez que ia no banco de dados
        return garageRepository
                .findAll()
                .stream()
                .collect(Collectors.toMap(Garage::getSector, g -> g)); //Melhor guardar ela e ver uma vez só :D
    }

    private Garage getGarage(ParkingSpotConfigDTO parkingSpotConfigDTO) {
        return garageRepository
                .findBySector(parkingSpotConfigDTO.sector())
                .orElseThrow(() -> new GarageNotFoundException(
                        "Garage with sector: " + parkingSpotConfigDTO.sector() + " was not found"
                ));
    }
}
