package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.NoAvailableGarageException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class WebhookEntryService {

    private final GarageRepository garageRepository;
    private final ParkingEventRepository parkingEventRepository;

    public void handleEntry(WebhookEventRequestDTO requestDTO) {

        log.info("[starts] WebhookEntryService - handleEntry()");

        if (requestDTO.entryTime() == null) {
            throw new InvalidRequestException("Entry time is required for ENTRY event");
        }

        Garage garage = garageRepository
                .findAvailable(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoAvailableGarageException("All Garages are full"));

        ParkingEvent parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate(requestDTO.licensePlate());
        parkingEvent.setEventType(EventTypeEnum.ENTRY);
        parkingEvent.setEntryTime(requestDTO.entryTime());
        parkingEvent.setDynamicPrice(garage.calculateDynamicPrice());

        parkingEventRepository.save(parkingEvent);

        log.info("Vehicle with plate '{}' entered.", parkingEvent);

        garage.incrementOccupancy();
        garageRepository.save(garage);

        log.info("Garage {} occupancy incremented: {} of {}", garage.getSector(), garage.getCurrentOccupancy(), garage.getMaxCapacity());
        log.info("[ends] WebhookEntryService - handleEntry()");
    }
}
