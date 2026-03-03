package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sector;

    @Column(name = "base_price")
    private double basePrice;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @OneToMany(mappedBy = "garage")
    private List<ParkingSpot> parkingSpots;
}
