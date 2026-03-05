package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.WebhookEventRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.InvalidRequestException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.GarageRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookEntryService entryService;
    private final WebhookParkedService parkedService;
    private final WebhookExitService exitService;


    @Transactional
    public void process(WebhookEventRequestDTO requestDTO) {
        log.info("[starts] WebhookService - process()");
        switch (requestDTO.eventType()) {
            case ENTRY -> entryService.handle(requestDTO);
            case PARKED -> parkedService.handle(requestDTO);
            case EXIT -> exitService.handle(requestDTO);
            default -> throw new InvalidRequestException("Invalid event type");
        }
    }
}
