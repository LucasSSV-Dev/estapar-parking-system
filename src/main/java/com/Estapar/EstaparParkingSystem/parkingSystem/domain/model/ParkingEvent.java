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

    @Id //Ia botar UUID, mas vi que a performance do IDENTITY é melhor
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "entry_time", nullable = false) //Hora que o carro entrou
    private LocalDateTime entryTime;

    @Column
    private BigDecimal discount;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

}
