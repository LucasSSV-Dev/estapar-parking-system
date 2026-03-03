package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "garage_sectors")
public class GarageSector {

    @Id
    @Column(length = 10, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private LocalTime openHour;

    @Column(nullable = false)
    private LocalTime closeHour;

    @Column(nullable = false)
    private int durationLimitMinutes;

    @Column(nullable = false)
    private int currentOccupancy = 0;



    // ===== Regras de domínio =====

    public boolean canAcceptNewVehicle() {
        return currentOccupancy < maxCapacity;
    }

    public void incrementOccupancy() {
        if (!canAcceptNewVehicle()) {
            throw new IllegalStateException("Sector is at maximum capacity");
        }
        currentOccupancy++;
    }

    public void decrementOccupancy() {
        if (currentOccupancy <= 0) {
            throw new IllegalStateException("Sector is already empty");
        }
        currentOccupancy--;
    }

    public BigDecimal calculateDynamicPrice() {
        if (maxCapacity == 0) {
            return basePrice;
        }

        double occupancyRate = (double) currentOccupancy / maxCapacity;

        if (occupancyRate >= 0.75) {
            return basePrice.multiply(BigDecimal.valueOf(1.25));
        } else if (occupancyRate >= 0.50) {
            return basePrice.multiply(BigDecimal.valueOf(1.10));
        } else if (occupancyRate >= 0.25) {
            return basePrice;
        } else {
            return basePrice.multiply(BigDecimal.valueOf(0.90));
        }
    }
}
