package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.OccupiedParkingSpotException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.ParkingSpotNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.VehicleNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class WebhookParkedService {

    private final ParkingEventRepository parkingEventRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    public void handle(WebhookEventRequestDTO requestDTO) {

        log.info("[starts] WebhookParkedService - handle()");

        if (requestDTO.lat() == null || requestDTO.lng() == null) {
            throw new InvalidRequestException("latitude and longitude are required for PARKED event");
        }

        ParkingEvent parkingEvent = parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(requestDTO.licensePlate())
                .orElseThrow(() -> new VehicleNotFoundException(requestDTO.licensePlate()));

        ParkingSpot spot = parkingSpotRepository
                .findByLatitudeAndLongitude(requestDTO.lat(), requestDTO.lng())
                .orElseThrow(() -> new ParkingSpotNotFoundException("Parking spot not found"));

        if (spot.isOccupied()) {
            throw new OccupiedParkingSpotException("Parking spot already occupied");
        }

        spot.setOccupied(true);
        spot.setCurrentLicensePlate(parkingEvent.getLicensePlate());
        parkingEvent.setSector(spot.getSector());

        parkingSpotRepository.save(spot);
        parkingEventRepository.save(parkingEvent);

        log.info("Parking spot occupied: {}", spot);
        log.info("ParkingEvent Parked: {}", parkingEvent);
        log.info("[ends] WebhookParkedService - handle()");
    }
}
