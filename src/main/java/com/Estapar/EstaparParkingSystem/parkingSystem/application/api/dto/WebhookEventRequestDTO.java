package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record WebhookEventRequestDTO(
        String licensePlate,
        @NotNull
        EventType eventType,
        @NotNull
        LocalDateTime entryTime,

        LocalDateTime exitTime,
        Double lat,
        Double lng
) {}
