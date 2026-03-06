package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.OccupiedParkingSpotException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.ParkingSpotNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.VehicleNotFoundException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingSpot;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
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
    private final GarageRepository garageRepository;

    public void handleParked(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookParkedService - handleParked()");

        if (requestDTO.lat() == null || requestDTO.lng() == null) {
            throw new InvalidRequestException("latitude and longitude are required for PARKED event");
        }

        //Procura o carro.
        ParkingEvent parkingEvent = parkingEventRepository
                .findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(requestDTO.licensePlate())
                .orElseThrow(() -> new VehicleNotFoundException(requestDTO.licensePlate()));

        //Procura uma vaga.
        ParkingSpot spot = parkingSpotRepository
                .findByLatitudeAndLongitude(requestDTO.lat(), requestDTO.lng())
                .orElseThrow(() -> new ParkingSpotNotFoundException("Parking spot not found"));

        //Se estiver ocupada
        if (spot.isOccupied()) {
            throw new OccupiedParkingSpotException("Parking spot already occupied");
        }

        //Ocupa a vaga
        spot.setOccupied(true);
        spot.setCurrentLicensePlate(parkingEvent.getLicensePlate());
        //Preenche os dados do evento
        parkingEvent.setSector(spot.getSector());
        parkingEvent.setEventType(EventTypeEnum.PARKED);
        parkingEvent.setDynamicPrice(spot.getGarage().calculateDynamicPrice());
        //Aumenta a currentOccupancy da garagem
        spot.getGarage().incrementOccupancy();


        parkingEventRepository.save(parkingEvent);
        parkingSpotRepository.save(spot);
        garageRepository.save(spot.getGarage());


        log.info("ParkingEvent created for plate {}", parkingEvent.getLicensePlate());
        log.info("Parking spot {} on sector {} occupied with plate: {}", spot.getId(), spot.getSector(), spot.getCurrentLicensePlate());
        log.info("Garage {} occupancy incremented: {} of {}", spot.getGarage().getSector(), spot.getGarage().getCurrentOccupancy(), spot.getGarage().getMaxCapacity());
        log.info("[ends] WebhookParkedService - handleParked()");
    }
}
