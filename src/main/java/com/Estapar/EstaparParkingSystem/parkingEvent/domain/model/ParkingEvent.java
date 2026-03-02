package com.Estapar.EstaparParkingSystem.parkingEvent.domain.model;

import com.Estapar.EstaparParkingSystem.parkingEvent.domain.enums.EventStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "parking_event")
public class ParkingEvent {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String licensePlate;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime entryTime;

    @Column
    private LocalDateTime exitTime;

    @Column
    private EventStatus status;

    public ParkingEvent(String licensePlate) {
        this.licensePlate = licensePlate;
        this.status = EventStatus.OPEN;
    }
}
