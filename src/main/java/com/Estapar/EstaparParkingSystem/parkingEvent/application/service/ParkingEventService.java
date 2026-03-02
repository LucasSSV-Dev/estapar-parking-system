package com.Estapar.EstaparParkingSystem.parkingEvent.application.service;

import com.Estapar.EstaparParkingSystem.parkingEvent.domain.model.ParkingEvent;

public interface ParkingEventService {

    ParkingEvent createParkingEvent(String licensePlate) throws Exception;

}
