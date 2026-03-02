package com.Estapar.EstaparParkingSystem.parkingEvent.application.service;

import com.Estapar.EstaparParkingSystem.parkingEvent.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingEvent.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingEvent.domain.validator.ParkingValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ParkingEventServiceImpl implements ParkingEventService {

    private final ParkingEventRepository parkingEventRepository;
    private final ParkingValidator parkingValidator;

    @Override
    public ParkingEvent createParkingEvent(
            @NotBlank
            String licensePlate
    ) throws Exception {
        log.info("[starts] ParkingEventServiceImpl -> createParkingEvent()");
        parkingValidator.notParked(licensePlate); //Valida se o carro já está estacionado

        ParkingEvent parkingEventEntity = new ParkingEvent(licensePlate);
        log.info("[ends] ParkingEventServiceImpl -> createParkingEvent()");
        return parkingEventRepository.save(parkingEventEntity);
    }

}
