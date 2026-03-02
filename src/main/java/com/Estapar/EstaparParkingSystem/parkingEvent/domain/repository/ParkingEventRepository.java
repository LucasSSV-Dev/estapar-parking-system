package com.Estapar.EstaparParkingSystem.parkingEvent.domain.repository;

import com.Estapar.EstaparParkingSystem.parkingEvent.domain.enums.EventStatus;
import com.Estapar.EstaparParkingSystem.parkingEvent.domain.model.ParkingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParkingEventRepository extends JpaRepository<ParkingEvent, UUID> {

    Optional<ParkingEvent> findByLicensePlateAndStatus(String licensePlate, EventStatus eventStatus);

    Optional<List<ParkingEvent>> findAllByEventStatus(EventStatus eventStatus);
}
