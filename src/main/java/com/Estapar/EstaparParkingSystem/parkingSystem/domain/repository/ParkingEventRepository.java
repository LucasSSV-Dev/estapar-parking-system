package com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.ParkingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
    Optional<ParkingEvent> findTopByLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(String licensePlate);

    @Query("""
    SELECT COALESCE(SUM(e.paidPrice), 0)
    FROM ParkingEvent e
    WHERE e.eventType = :eventType
      AND e.sector = :sector
      AND e.exitTime >= :startOfDay
      AND e.exitTime < :endOfDay
""")
    Optional<BigDecimal> sumRevenueByDateAndSectorAndEventType(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("sector") String sector,
            @Param("eventType") EventTypeEnum eventType
    );
}