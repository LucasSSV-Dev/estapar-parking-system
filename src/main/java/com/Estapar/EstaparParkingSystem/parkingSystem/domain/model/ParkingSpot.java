package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
//Evita que eu cadastre uma vaga que tenha a mesma combinação dos 3)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"sector", "latitude", "longitude"}))
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String sector;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column(nullable = false)
    private boolean occupied;

    @Column(name = "current_license_plate") // É só o que preciso mesmo xD
    private String currentLicensePlate;

    @ManyToOne
    @JoinColumn(name = "garage_id")
    private Garage garage;
}
