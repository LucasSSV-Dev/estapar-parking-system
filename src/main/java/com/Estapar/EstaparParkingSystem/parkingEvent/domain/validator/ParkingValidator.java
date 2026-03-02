package com.Estapar.EstaparParkingSystem.parkingEvent.domain.validator;

import com.Estapar.EstaparParkingSystem.parkingEvent.domain.enums.EventStatus;
import com.Estapar.EstaparParkingSystem.parkingEvent.domain.model.ParkingEvent;
import com.Estapar.EstaparParkingSystem.parkingEvent.domain.repository.ParkingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ParkingValidator {

    private final ParkingEventRepository parkingEventRepository;

    public void notParked(String licensePlate) throws Exception {
        if (isAlreadyParked(licensePlate)) {
            throw new Exception("Carro com placa: "+ licensePlate +", já está estacionado");
        }
    }

    private boolean isAlreadyParked(String licensePlate) {
        Optional<ParkingEvent> newParkingRequest = parkingEventRepository.findByLicensePlateAndStatus(licensePlate, EventStatus.OPEN);
        return newParkingRequest.isPresent();
    }

}
