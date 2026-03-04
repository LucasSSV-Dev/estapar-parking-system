package com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto;

import java.time.LocalDate;

public record RevenueRequestDTO(
        LocalDate date,
        String sector
) {}
