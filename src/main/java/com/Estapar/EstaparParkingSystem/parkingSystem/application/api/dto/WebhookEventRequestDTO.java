package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record WebhookEventRequestDTO(
        @JsonProperty("license_plate")
        @NotNull
        String licensePlate,

        @JsonProperty("event_type")
        @NotNull
        EventType eventType,

        @JsonProperty("entry_time")
        LocalDateTime entryTime,

        @JsonProperty("exit_time")
        LocalDateTime exitTime,

        Double lat,

        Double lng
) {}
