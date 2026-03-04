package com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, UUID> {

    Optional<Revenue> findBySectorAndDate(String sector, LocalDate date);

}
