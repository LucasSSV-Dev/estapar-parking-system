package com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Garage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {

    @Query("""
   select g from Garage g
   where g.currentOccupancy < g.maxCapacity
   order by g.currentOccupancy asc
""")
    List<Garage> findAvailable(PageRequest pageRequest);
}
