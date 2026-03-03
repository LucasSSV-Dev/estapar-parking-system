package com.Estapar.EstaparParkingSystem.parkingSystem.domain.model;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "parking_event")
public class ParkingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Ia botar UUID mas vi que a perfomance do IDENTITY é melhor
    private Long id; //Identificador no sistema

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType type;

    @Column(name = "entry_time", nullable = false) //Hora que o carro entrou
    private LocalDateTime entryTime;

    @Column(name = "entry_time")
    private LocalDateTime exitTime; //Retirada do carro... Vou guardar pra fazer um histórico se pá

    @Column(name = "sector_id")
    private String sectorId;

    @Column
    private BigDecimal price;

}
