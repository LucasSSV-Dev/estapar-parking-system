package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class WebhookEntryService {

    private final ParkingEventRepository parkingEventRepository;

    public void handleEntry(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookEntryService - handleEntry()");

        if (requestDTO.entryTime() == null) {
            throw new InvalidRequestException("Entry time is required for ENTRY event");
        }

        ParkingEvent parkingEvent = new ParkingEvent();
        parkingEvent.setLicensePlate(requestDTO.licensePlate());
        parkingEvent.setEventType(EventTypeEnum.ENTRY);
        parkingEvent.setEntryTime(requestDTO.entryTime());

        parkingEventRepository.save(parkingEvent);

        log.info("Vehicle with plate '{}' entered.", parkingEvent.getLicensePlate());
        log.info("[ends] WebhookEntryService - handleEntry()");
    }
}
