package com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
    Optional<ParkingEvent> findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(String licensePlate);
}