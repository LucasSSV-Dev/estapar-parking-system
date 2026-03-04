package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.CurrencyEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final ParkingEventRepository parkingEventRepository;

    public RevenueResponseDTO calculateRevenue(RevenueRequestDTO revenueRequestDTO) {

        Interval interval = getDateStartAndEnd(revenueRequestDTO);

        BigDecimal total = parkingEventRepository
                .sumRevenueByDateAndSectorAndEventType(
                        interval.start(),
                        interval.end(),
                        revenueRequestDTO.sector(),
                        EventTypeEnum.EXIT
                ).orElse(BigDecimal.ZERO);

        return new RevenueResponseDTO(
                total,
                CurrencyEnum.BRL,
                Instant.now()
        );
    }



    private static Interval getDateStartAndEnd(RevenueRequestDTO dto) {
        LocalDateTime start = dto.date().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return new Interval(start, end);
    }

    private record Interval(LocalDateTime start, LocalDateTime end) {
    }
}
