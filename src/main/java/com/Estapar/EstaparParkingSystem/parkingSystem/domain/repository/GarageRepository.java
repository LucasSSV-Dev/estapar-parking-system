package com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {

    Optional<Garage> findBySector(String sector);

}
