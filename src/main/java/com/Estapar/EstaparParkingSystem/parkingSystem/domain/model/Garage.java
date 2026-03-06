package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.NoParkingSpotsAvailableException;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.exception.ParkingSpotNotFoundException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Data
@Table
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sector;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @Column(name = "current_occupancy")
    private int currentOccupancy = 0;

    @OneToMany(mappedBy = "garage")
    private List<ParkingSpot> parkingSpots;


    public void decrementOccupancy() {
        if (currentOccupancy == 0) {
            throw new ParkingSpotNotFoundException("Garage is already empty");
        }
        currentOccupancy--;
    }

    public void incrementOccupancy() {
        if (currentOccupancy >= maxCapacity) {
            throw new NoParkingSpotsAvailableException("Garage is full");
        }
        currentOccupancy++;
    }

    public BigDecimal calculateDynamicPrice() {
        double rate = (double) currentOccupancy / maxCapacity;
        if (rate < 0.25) {
            return BigDecimal.valueOf(0.90).setScale(2, RoundingMode.HALF_UP);
        } else if (rate < 0.50) {
            return BigDecimal.valueOf(1.00).setScale(2, RoundingMode.HALF_UP);
        } else if (rate < 0.75) {
            return BigDecimal.valueOf(1.10).setScale(2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.valueOf(1.25).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
