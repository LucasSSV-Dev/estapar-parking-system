package com.Estapar.EstaparParkingSystem.parkingSystem.application.service;

import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.IntervalDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueRequestDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.application.api.dto.RevenueResponseDTO;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.enums.EventTypeEnum;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.model.Revenue;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.ParkingEventRepository;
import com.Estapar.EstaparParkingSystem.parkingSystem.domain.repository.RevenueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class RevenueService {

    private final ParkingEventRepository parkingEventRepository;
    private final RevenueRepository revenueRepository;

    @Transactional
    public RevenueResponseDTO calculateRevenue(RevenueRequestDTO revenueRequestDTO) {
        log.info("[starts] RevenueService - calculateRevenue()");

        //Calculo no banco de dados
        IntervalDTO interval = getDateStartAndEnd(revenueRequestDTO);
        BigDecimal amount = parkingEventRepository
                .sumRevenueByDateAndSectorAndEventType(
                        interval.start(),
                        interval.end(),
                        revenueRequestDTO.sector(),
                        EventTypeEnum.EXIT
                ).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.DOWN);

        //Vejo se já foi realizado o cálculo para essa data e setor pra não criar múltiplas receitas
        Revenue revenue = revenueRepository.findBySectorAndDate(
                revenueRequestDTO.sector(),
                revenueRequestDTO.date()
        ).orElse(new Revenue());

        if (revenue.getId() == null) {//Se ela for nova:
            revenue.setDate(revenueRequestDTO.date());
            revenue.setSector(revenueRequestDTO.sector());
        }
        revenue.setAmount(amount);
        revenueRepository.save(revenue);
        revenueRepository.flush();

        log.info("[ends] RevenueService - calculateRevenue()\n");
        return revenue.toResponseDTO();
    }

    private static IntervalDTO getDateStartAndEnd(RevenueRequestDTO dto) {
        LocalDateTime start = dto.date().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return new IntervalDTO(start, end);
    }
}
