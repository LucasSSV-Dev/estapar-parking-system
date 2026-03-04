package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
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
        currentOccupancy--;
    }

    public void incrementOccupancy() {
        currentOccupancy++;
    }

    public BigDecimal calculateDynamicPrice() {
        if (currentOccupancy >= maxCapacity) {
            throw new IllegalStateException("Sector is full");
        }
        double rate = (double) currentOccupancy / maxCapacity;
        if (rate < 0.25) {
            return BigDecimal.valueOf(0.90);
        } else if (rate < 0.50) {
            return BigDecimal.valueOf(1.00);
        } else if (rate < 0.75) {
            return BigDecimal.valueOf(1.10);
        } else {
            return BigDecimal.valueOf(1.25);
        }
    }
}
